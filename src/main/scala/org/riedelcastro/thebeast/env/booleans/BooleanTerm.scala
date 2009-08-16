package org.riedelcastro.thebeast.env.booleans


import doubles.BoolToDoubleCast
import functions._

/**
 * @author Sebastian Riedel
 */

trait BooleanTerm extends BoundedTerm[Boolean] {
  def @@ = BoolToDoubleCast(this)

  def &&(rhs: BooleanTerm) = AndApp(this, rhs)

  def ~>(rhs: BooleanTerm) = ImpliesApp(this, rhs)

  def ground(env: Env): BooleanTerm


}

case class BooleanConstant(override val value: Boolean) extends BoundedConstant(value) with BooleanTerm {
  override def ground(env: Env) = this
}

case class BooleanFunApp[T](override val function: Term[T => Boolean], override val arg: Term[T])
        extends FunApp(function, arg) with BooleanTerm {
  def upperBound = true


  override def ground(env: Env) = BooleanFunApp(function.ground(env), arg.ground(env))
}


case class AndApp(lhs: Term[Boolean], rhs: Term[Boolean]) extends BooleanFunApp(FunApp(Constant(And), lhs), rhs)

case class ImpliesApp(lhs: BooleanTerm, rhs: BooleanTerm) extends BooleanFunApp(FunApp(Constant(Implies), lhs), rhs)

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


