package org.riedelcastro.thebeast.env.doubles


import booleans.BooleanTerm
import functions._
import solve.ExhaustiveMarginalInference
import tuples.TupleTerm2
import vectors.{VectorTerm, VectorScalarApp}

/**
 * @author Sebastian Riedel
 */

trait DoubleTerm extends BoundedTerm[Double] {
  def +(rhs: DoubleTerm) = AddApp(this, rhs)

  def *(rhs: DoubleTerm) = TimesApp(this, rhs)

  def *(rhs: VectorTerm) = VectorScalarApp(rhs, this)

  def marginalize(incoming: Beliefs): Beliefs = {
    val term = this * Product(variables.map(v => BeliefTerm(incoming.belief(v), v)).toSeq)
    ExhaustiveMarginalInference.infer(term)
  }

  //def ground(env:Env) : DoubleTerm = super.ground(env).asInstanceOf[DoubleTerm]

  def ground(env: Env): DoubleTerm
}


case class CPD[O, C](conditioned: ConditionedTerm[O, C], parameters: Term[Tuple2[O, C] => Double])
        extends DoubleFunApp(parameters, TupleTerm2(conditioned.term, conditioned.condition)) {
}



case class AddApp(lhs: DoubleTerm, rhs: DoubleTerm) extends DoubleFunApp(FunApp(Constant(Add), lhs), rhs) {
  override def ground(env: Env) = AddApp(lhs.ground(env), rhs.ground(env))
}

case class TimesApp(lhs: DoubleTerm, rhs: DoubleTerm) extends DoubleFunApp(FunApp(Constant(Times), lhs), rhs) {
  override def upperBound = Math.max(lhs.upperBound * rhs.upperBound, 0.0)
}
case class Sum(override val args: Seq[DoubleTerm]) extends Fold[Double](Constant(Add), args, Constant(0.0))
        with DoubleTerm {
  def upperBound = args.foldLeft(0.0) {(b, a) => b + a.upperBound}

  override def ground(env: Env) = Sum(args.map(a => a.ground(env)))

}

object SumHelper {
  def sum(terms: Collection[DoubleTerm], env: Env) = terms.foldLeft(0.0) {(s, t) => s + env(t)}
}

case class Product(override val args: Seq[DoubleTerm]) extends Fold[Double](Constant(Times), args, Constant(0))
        with DoubleTerm {
  override def ground(env: Env) = Product(args.map(a => a.ground(env)))

  def upperBound = args.foldLeft(1.0) {(b, a) => b * Math.abs(a.upperBound)}


}


case class QuantifiedSum[T](override val variable: Var[T], override val formula: DoubleTerm)
        extends Quantification(Constant(Add), variable, formula, Constant(0.0)) with DoubleTerm {
  override lazy val unroll = {
    val env = new MutableEnv
    Sum(variable.values.map(value => {env += variable -> value; formula.ground(env)}).toSeq)
  }

  def upperBound = unroll.upperBound

  override def ground(env: Env) = unroll.ground(env)

}
case class BoolToDoubleCast(boolTerm: BooleanTerm) extends FunApp(Constant(CastBoolToDouble), boolTerm)
        with DoubleTerm {
  def upperBound = if (boolTerm.upperBound) 1.0 else 0.0

  override def ground(env: Env) = BoolToDoubleCast(boolTerm.ground(env))
}

case class DoubleFunApp[T](override val function: Term[T => Double], override val arg: Term[T])
        extends FunApp(function, arg) with DoubleTerm {
  def upperBound = Math.POS_INF_DOUBLE

  override def ground(env: Env) = DoubleFunApp(function.ground(env), arg.ground(env))
}

case class DoubleConstant(override val value: Double) extends BoundedConstant(value) with DoubleTerm {
  //problem Constant superclass defines ground to return Constants

  override def ground(env: Env) = this
}

object Add extends (Double => (Double => Double)) {
  def apply(arg1: Double): (Double => Double) = (arg2: Double) => arg1 + arg2

  override def toString = "Add"
}

object Times extends (Double => (Double => Double)) {
  def apply(arg1: Double): (Double => Double) = (arg2: Double) => arg1 * arg2

  override def toString = "Times"
}

object CastBoolToDouble extends (Boolean => Double) {
  def apply(bool: Boolean) = if (bool) 1.0 else 0.0

  override def toString = "B2D"
}

