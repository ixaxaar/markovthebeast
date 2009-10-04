package org.riedelcastro.thebeast.env.booleans


import doubles.Indicator
import functions._
import util.{Util, SimpleNamed}

/**
 * @author Sebastian Riedel
 */

trait BooleanTerm extends BoundedTerm[Boolean] {
  def @@ = Indicator(this)

  def &&(rhs: BooleanTerm) = AndApp(this, rhs)

  def ~>(rhs: BooleanTerm) = ImpliesApp(this, rhs)

  def <~>(rhs: BooleanTerm) = EquivalenceApp(this, rhs)

  def ground(env: Env): BooleanTerm


  def simplify:BooleanTerm

  lazy val toCNF: CNF = moveInNegation.distributeAnds.flatten match {
    case x: BooleanConstant => CNF(Seq(Disjunction(Seq(x))))
    case Conjunction(args) => CNF(args.map(a => a match {
      case x: Disjunction[_] => x.flatten
      case x => Disjunction(Seq(x))
    })).trim
    case x: NotApp => CNF(Seq(Disjunction(Seq(x))))
    case x => error("After moving in negations and distributing ands the term must be a constant or conjunction" +
            " pr a literal and not a " + x)
  }

  def negate: BooleanTerm

  def moveInNegation: BooleanTerm

  def distributeAnds: BooleanTerm = this

  def flatten: BooleanTerm

}

case class Conjunction[+T <: BooleanTerm](override val args: Seq[T]) extends Fold[Boolean](Constant(And), args, Constant(true))
        with BooleanTerm {
  override def ground(env: Env) = Conjunction(args.map(_.ground(env)))

  def upperBound = !args.exists(!_.upperBound)

  def negate = Disjunction(args.map(_.negate))

  override def distributeAnds = Conjunction(args.map(_.distributeAnds))

  def flatten = Conjunction(args.flatMap(a => a.flatten match {case Conjunction(inner) => inner; case _ => Seq(a)}))

  def moveInNegation = Conjunction(args.map(_.moveInNegation))


  override def toString = args.mkString("("," & ", ")")
}



case class Disjunction[+T <: BooleanTerm](override val args: Seq[T]) extends Fold[Boolean](Constant(Or), args, Constant(false))
        with BooleanTerm {
  override def ground(env: Env) = Disjunction(args.map(_.ground(env)))

  def upperBound = args.exists(_.upperBound)

  def negate = Conjunction(args.map(_.negate))

  override def distributeAnds = {
    val groups = args.map(_.distributeAnds).map(a => a match {
      case Conjunction(args) => args.toStream
      case _ => Stream(a)
    })
    Conjunction(Util.Cartesian.cartesianProduct(groups).map(Disjunction(_)).toSeq)
  }

  def flatten = Disjunction(args.flatMap(a => a.flatten match {case Disjunction(inner) => inner; case _ => Seq(a)}))

  def moveInNegation = Disjunction(args.map(_.moveInNegation))

  override def toString = args.mkString("("," | ", ")")
  

}

case class CNF(override val args: Seq[Disjunction[BooleanTerm]]) extends Conjunction(args) {
  override def ground(env: Env): CNF = CNF(args.map(_.ground(env).asInstanceOf[Disjunction[BooleanTerm]]))

  def trim = CNF(args.filter(d=> !d.args.exists(x=>d.args.exists(y=>x == NotApp(y)))))
}

case class DNF(override val args: Seq[Conjunction[BooleanTerm]]) extends Disjunction(args) {
  override def ground(env: Env): DNF = DNF(args.map(_.ground(env).asInstanceOf[Conjunction[BooleanTerm]]))
}


case class BooleanConstant(override val value: Boolean) extends BoundedConstant(value) with BooleanTerm {
  override def ground(env: Env) = this

  def negate = BooleanConstant(!value)

  override def distributeAnds = this

  def moveInNegation = this

  def flatten = this

  override def simplify = this
}

case class BooleanFunApp[T](override val function: Term[T => Boolean], override val arg: Term[T])
        extends FunApp(function, arg) with BooleanTerm {
  def upperBound = true

  //todo: this is bad, ideally this should remain empty here and in FunApp
  override def ground(env: Env): BooleanTerm = BooleanFunApp(function.ground(env),arg.ground(env))

  def negate: BooleanTerm = NotApp(this)

  def flatten = this

  def moveInNegation: BooleanTerm = this

  override def simplify:BooleanTerm =
    function.simplify match {
      case Constant(f) => arg.simplify match {
        case Constant(x) => BooleanConstant(f(x));
        case x => BooleanFunApp(Constant(f), x)
      }
      case f => BooleanFunApp(f, arg.simplify)
    }
}


case class AndApp(lhs: BooleanTerm, rhs: BooleanTerm) extends Conjunction(Seq(lhs, rhs)) {
}
case class OrApp(lhs: BooleanTerm, rhs: BooleanTerm) extends Disjunction(Seq(lhs, rhs)) {
}

case class NotApp(override val arg: BooleanTerm) extends BooleanFunApp(Constant(Not), arg) {

  override def negate = arg

  override def ground(env: Env) = NotApp(arg.ground(env))

  override def distributeAnds = NotApp(arg.distributeAnds)

  override def flatten = NotApp(arg.flatten)

  override def moveInNegation = arg.negate

  override def toString = "!" + arg

}

case class ImpliesApp(lhs: BooleanTerm, rhs: BooleanTerm) extends Disjunction(Seq(NotApp(lhs), rhs)) {
  override def toString = lhs + "=>" + rhs
}

case class EquivalenceApp(lhs: BooleanTerm, rhs: BooleanTerm)
        extends Disjunction(Seq(Conjunction(Seq(lhs, rhs)), Conjunction(Seq(NotApp(lhs), NotApp(rhs))))){
  override def toString = lhs + "<=>" + rhs

}

trait BooleanBinaryOperator extends (Boolean => (Boolean => Boolean)) with SimpleNamed

trait BooleanUnaryOperator extends (Boolean => Boolean) with SimpleNamed

object And extends BooleanBinaryOperator {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => arg1 && arg2
}

object Or extends BooleanBinaryOperator {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => arg1 || arg2
}

object Implies extends BooleanBinaryOperator {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => !arg1 || arg2
}

object Not extends BooleanUnaryOperator {
  def apply(arg1: Boolean): Boolean = !arg1
}

object Equivalence extends BooleanBinaryOperator {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => arg1 == arg2
}

