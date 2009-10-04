package org.riedelcastro.thebeast.env.ints


import booleans.BooleanFunApp
import functions.BoundedConstant

/**
 * @author Sebastian Riedel
 */

trait IntTerm extends BoundedTerm[Int] {

  def < (that:IntTerm) = BooleanFunApp(FunApp(Constant(IntLT),this), that)
}


case class IntConstant(override val value: Int) extends BoundedConstant(value) with IntTerm {
  override def ground(env: Env) = this
}

object IntAdd extends (Int => (Int => Int)) {
  def apply(arg1: Int): (Int => Int) = (arg2: Int) => arg1 + arg2

  override def toString = "IntAdd"
}

object IntLT extends (Int => (Int => Boolean)) {
  def apply(arg1: Int): (Int => Boolean) = (arg2: Int) => arg1 < arg2

  override def toString = "IntLT"
}

