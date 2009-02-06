package org.riedelcastro.thebeast
/**
 * @author Sebastian Riedel
 */

trait Term[+T] {
  def resolve(binding: Binding): T
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