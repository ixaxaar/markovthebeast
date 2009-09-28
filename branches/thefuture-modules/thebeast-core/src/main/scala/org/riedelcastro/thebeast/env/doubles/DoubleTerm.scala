package org.riedelcastro.thebeast.env.doubles


import booleans.BooleanTerm
import collection.mutable.{HashSet, HashMap}
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
    val term = this * Multiplication(variables.map(v => BeliefTerm(incoming.belief(v), v)).toSeq)
    ExhaustiveMarginalInference.infer(term)
  }

  //def ground(env:Env) : DoubleTerm = super.ground(env).asInstanceOf[DoubleTerm]

  def ground(env: Env): DoubleTerm
}


case class CPDParams[O,C](outcomeValues:Values[O], conditionValues:Values[C], mapping:Pair[(O,C),Double]*)
        extends HashMap[(O,C),Double] {
  for (pair <- mapping) this += pair

  for (condition <- conditionValues) {
    val undefined = new HashSet[O]
    var totalProb = 0.0
    for (outcome <- outcomeValues) {
      this.get((outcome,condition)) match {
        case Some(prob) => totalProb += prob
        case None => undefined += outcome
      }
    }
    val probOfRest = (1.0 - totalProb) / undefined.size
    for (outcome <- undefined) {
      this += (outcome,condition)-> probOfRest
    }
  }
}

case class PriorPDParams[O](override val outcomeValues:Values[O], singleMapping:Pair[O,Double]*)
        extends CPDParams(outcomeValues,Singleton,singleMapping.map(pair=>((pair._1,Singleton),pair._2)):_*)

case class CPD[O, C](conditioned: ConditionedTerm[O, C], parameters: Term[Tuple2[O, C] => Double])
        extends DoubleFunApp(parameters, TupleTerm2(conditioned.term, conditioned.condition)) {
}

case class PriorPD[O](outcome:Term[O], override val parameters: Term[Tuple2[O, SingletonClass] => Double])
        extends CPD(ConditionedTerm(outcome,Singleton),parameters) {
}


case class AddApp(lhs: DoubleTerm, rhs: DoubleTerm) extends DoubleFunApp(FunApp(Constant(Add), lhs), rhs) {
  override def ground(env: Env) = AddApp(lhs.ground(env), rhs.ground(env))
}

case class TimesApp(lhs: DoubleTerm, rhs: DoubleTerm) extends DoubleFunApp(FunApp(Constant(Times), lhs), rhs) {
  override def upperBound = Math.max(lhs.upperBound * rhs.upperBound, 0.0)

  override def toString = "(" + lhs + "*" + rhs + ")"
}
case class Sum[+T<:DoubleTerm](override val args: Seq[T]) extends Fold[Double](Constant(Add), args, Constant(0.0))
        with DoubleTerm {
  def upperBound = args.foldLeft(0.0) {(b, a) => b + a.upperBound}

  override def ground(env: Env) = Sum(args.map(a => a.ground(env)))


  override def toString = args.mkString("(","+",")")
}

object SumHelper {
  def sum(terms: Collection[DoubleTerm], env: Env) = terms.foldLeft(0.0) {(s, t) => s + env(t)}
}

case class Multiplication(override val args: Seq[DoubleTerm]) extends Fold[Double](Constant(Times), args, Constant(1.0))
        with DoubleTerm {
  override def ground(env: Env) = Multiplication(args.map(a => a.ground(env)))

  def upperBound = args.foldLeft(1.0) {(b, a) => b * Math.abs(a.upperBound)}


  override def toString = "Multiplication(" + args + ")"
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
case class Indicator(boolTerm: BooleanTerm) extends FunApp(Constant(CastBoolToDouble), boolTerm)
        with DoubleTerm {
  def upperBound = if (boolTerm.upperBound) 1.0 else 0.0

  override def ground(env: Env) = Indicator(boolTerm.ground(env))


  override def toString = "[[" + boolTerm + "]]"
}

case class AlchemyIndicator(boolTerm:BooleanTerm)
        extends Sum(boolTerm.toCNF.args.map(a=>TimesApp(Indicator(a),DoubleConstant(1.0/boolTerm.toCNF.args.size)))){
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

