package org.riedelcastro.thebeast.env


import collection.mutable.HashMap

/**
 * @author Sebastian Riedel
 */

trait Beliefs {
  def belief[T](term:Term[T]) : Belief[T]
}

class MutableBeliefs extends Beliefs  {
  private val beliefs = new HashMap[Term[Any],Belief[Any]]
  
  def setBelief[T](term:Term[T],belief:Belief[T]) = {
    beliefs(term.asInstanceOf[Term[Any]]) = belief.asInstanceOf[Belief[Any]]
  }

  def increaseBelief[T](term:Term[T], value:T, delta:Double) = {
    var belief = beliefs.getOrElseUpdate(term,
      new MutableBelief[T](term.values).asInstanceOf[MutableBelief[Any]]).asInstanceOf[MutableBelief[T]]
    belief.increaseBelief(value,delta)
  }

  def belief[T](term: Term[T]) : Belief[T] = beliefs(term).asInstanceOf[Belief[T]]
}

sealed trait Belief[T] {
  def values:Values[T]
  def belief(value:T) : Double
}

case class Ignorance[T](val values:Values[T]) extends Belief[T] {
  def belief(value: T) = 1.0
}

class MutableBelief[T](val values:Values[T]) extends Belief[T] {
  private val _belief = new HashMap[T,Double]

  def belief(value: T) = _belief(value)

  def setBelief(value:T, belief:Double) = _belief(value) = belief

  def increaseBelief(value:T, belief:Double) = _belief(value) = _belief.getOrElse(value, 0.0) + belief

}
