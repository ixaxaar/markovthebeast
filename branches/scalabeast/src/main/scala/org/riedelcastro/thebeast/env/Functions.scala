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


case class BoundedConstant[T](override val value: T) extends Constant(value) with BoundedTerm[T]{
  override def upperBound = value
}

case class DoubleConstant(override val value: Double) extends BoundedConstant(value) with DoubleTerm
case class BooleanConstant(override val value: Boolean) extends BoundedConstant(value) with BooleanTerm
case class IntConstant(override val value: Int) extends BoundedConstant(value) with IntTerm


case class BoolToDoubleCast(boolTerm: BooleanTerm) extends FunApp(Constant(CastBoolToDouble), boolTerm)
        with DoubleTerm {
  def upperBound = if (boolTerm.upperBound) 1.0 else 0.0
}

case class AddApp(lhs: Term[Double], rhs: Term[Double]) extends FunApp(FunApp(Constant(Add), lhs), rhs)
case class AndApp(lhs: Term[Boolean], rhs: Term[Boolean]) extends FunApp(FunApp(Constant(And), lhs), rhs)
case class ImpliesApp(lhs: Term[Boolean], rhs: Term[Boolean]) extends FunApp(FunApp(Constant(Implies), lhs), rhs)
case class TimesApp(lhs: Term[Double], rhs: Term[Double]) extends FunApp(FunApp(Constant(Times), lhs), rhs)
case class Sum(override val args: Seq[Term[Double]]) extends Fold[Double](Constant(Add), args, Constant(0))
case class Product(override val args: Seq[Term[Double]]) extends Fold[Double](Constant(Times), args, Constant(0))


