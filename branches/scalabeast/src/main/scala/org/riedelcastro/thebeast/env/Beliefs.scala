package org.riedelcastro.thebeast.env


import collection.mutable.HashMap

/**
 * @author Sebastian Riedel
 */

trait Beliefs {
  def belief[T](term:Term[T]) : Belief[T]  
}

class MutableBeliefs extends Beliefs {
  private val beliefs = new HashMap[Term[Any],Any]
  
  def setBelief[T](term:Term[T],belief:Belief[T]) = {
    beliefs(term) = belief
  }

  def belief[T](term: Term[T]) = beliefs(term).asInstanceOf[Belief[T]]
}

sealed trait Belief[T] {
  def values:Values[T]
  def belief(value:T) : Double
}

