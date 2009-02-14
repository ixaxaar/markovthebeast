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

case class TupleTerm1[+T1](val _1: Term[T1]) extends Term[Tuple1[T1]] {
  def resolve(binding: Binding) = Tuple1(_1.resolve(binding))
}

case class TupleTerm2[+T1,+T2](val _1: Term[T1], val _2:Term[T2]) extends Term[Tuple2[T1,T2]] {
  def resolve(binding: Binding) = Tuple2(_1.resolve(binding),_2.resolve(binding))
}


case class Constant[+T](val value: T) extends Term[T] {
  def resolve(binding: Binding) = value
}

case class FunApp[T, R](val f: T => R, val arg: Term[T]) extends Term[R] {
  def resolve(binding: Binding) = f(arg.resolve(binding))
}