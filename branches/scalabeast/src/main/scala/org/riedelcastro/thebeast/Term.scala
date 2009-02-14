package org.riedelcastro.thebeast

import _root_.scala.collection.mutable.HashMap

/**
 * @author Sebastian Riedel
 */

trait Term[+T] {
  def resolve(binding: Binding): T
}

class Binding {
  private[this] val bindings = HashMap[Variable[Any], Any]()

  def get[T](variable: Variable[T]): T = bindings(variable).asInstanceOf[T]

  def set[T](variable: Variable[T], value: T) = bindings += (variable -> value)
}

trait Type[T] {
  def elements(): Stream[T]
}

case class Variable[+T](val name: String) extends Term[T] {
  def resolve(binding: Binding) = binding.get(this)
}

case class Constant[+T](val value: T) extends Term[T] {
  def resolve(binding: Binding) = value
}

case class FunApp[T, R](val f: T => R, val arg: Term[T]) extends Term[R] {
  def resolve(binding: Binding) = f(arg.resolve(binding))
}