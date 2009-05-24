package org.riedelcastro.thebeast.env

/**
 * @author Sebastian Riedel
 */


class EQ[T] extends (T => (T => Boolean)) {
  def apply(lhs: T): (T => Boolean) = (rhs: T) => lhs == rhs;
  override def toString = "Equals"
}

object IntAdd extends (Int => (Int => Int)) {
  def apply(arg1: Int): (Int => Int) = (arg2: Int) => arg1 + arg2

  override def toString = "IntAdd"
}

object Add extends (Double => (Double => Double)) {
  def apply(arg1: Double): (Double => Double) = (arg2: Double) => arg1 + arg2

  override def toString = "Add"
}

object Times extends (Double => (Double => Double)) {
  def apply(arg1: Double): (Double => Double) = (arg2: Double) => arg1 * arg2

  override def toString = "Times"
}


object And extends (Boolean => (Boolean => Boolean)) {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => arg1 && arg2

  override def toString = "And"
}

object Or extends (Boolean => (Boolean => Boolean)) {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => arg1 || arg2

  override def toString = "Or"
}

object Implies extends (Boolean => (Boolean => Boolean)) {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => !arg1 || arg2

  override def toString = "Implies"
}


object CastBoolToDouble extends (Boolean => Double) {
  def apply(bool: Boolean) = if (bool) 1.0 else 0.0

  override def toString = "B2D"
}


case class BoundedConstant[T](override val value: T) extends Constant(value) with BoundedTerm[T] {
  //override type Type = BoundedConstant[T]
  override def upperBound = value
}

case class DoubleConstant(override val value: Double) extends BoundedConstant(value) with DoubleTerm {
  //problem Constant superclass defines ground to return Constants

  override def ground(env: Env) = this
}
case class BooleanConstant(override val value: Boolean) extends BoundedConstant(value) with BooleanTerm {
  override def ground(env: Env) = this
}
case class IntConstant(override val value: Int) extends BoundedConstant(value) with IntTerm {
  override def ground(env: Env) = this
}

case class DoubleFunApp[T](override val function: Term[T => Double], override val arg: Term[T])
        extends FunApp(function, arg) with DoubleTerm {
  def upperBound = Math.POS_INF_DOUBLE

  override def ground(env: Env) = DoubleFunApp(function.ground(env),arg.ground(env))
}

case class BooleanFunApp[T](override val function: Term[T => Boolean], override val arg: Term[T])
        extends FunApp(function, arg) with BooleanTerm {
  def upperBound = true


  override def ground(env: Env) = BooleanFunApp(function.ground(env),arg.ground(env))
}

case class BoolToDoubleCast(boolTerm: BooleanTerm) extends FunApp(Constant(CastBoolToDouble), boolTerm)
        with DoubleTerm {
  def upperBound = if (boolTerm.upperBound) 1.0 else 0.0

  override def ground(env: Env) = BoolToDoubleCast(boolTerm.ground(env))
}

case class AddApp(lhs: DoubleTerm, rhs: DoubleTerm) extends DoubleFunApp(FunApp(Constant(Add), lhs), rhs) {
  override def ground(env: Env) = AddApp(lhs.ground(env), rhs.ground(env))
}
case class AndApp(lhs: Term[Boolean], rhs: Term[Boolean]) extends BooleanFunApp(FunApp(Constant(And), lhs), rhs)

case class ImpliesApp(lhs: BooleanTerm, rhs: BooleanTerm) extends BooleanFunApp(FunApp(Constant(Implies), lhs), rhs)

case class TimesApp(lhs: DoubleTerm, rhs: DoubleTerm) extends DoubleFunApp(FunApp(Constant(Times), lhs), rhs) {
  override def upperBound = Math.max(lhs.upperBound * rhs.upperBound, 0.0)
}
case class Sum(override val args: Seq[DoubleTerm]) extends Fold[Double](Constant(Add), args, Constant(0.0))
  with DoubleTerm{
  def upperBound = args.foldLeft(0.0){(b,a)=> b + a.upperBound }

  override def ground(env: Env) = Sum(args.map(a => a.ground(env)))
}
case class Product(override val args: Seq[Term[Double]]) extends Fold[Double](Constant(Times), args, Constant(0))


case class QuantifiedSum[T](override val variable: Var[T], override val formula: DoubleTerm)
        extends Quantification(Constant(Add), variable, formula, Constant(0.0)) with DoubleTerm {
  override lazy val unroll = {
    val env = new MutableEnv
    Sum(variable.values.map(value => {env += variable -> value; formula.ground(env)}).toSeq)
  }
  def upperBound = unroll.upperBound
  override def ground(env: Env) = unroll.ground(env)

}
