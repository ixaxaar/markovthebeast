package org.riedelcastro.thebeast.util

import collection.mutable.{HashMap, ArrayBuffer}
import env._
import functions._


class Node[T <: Term[_]](val variable: EnvVar[_]) {
  val factors = new ArrayBuffer[Factor[T]] with RandomDrawable[Factor[T]]
}

class Factor[T <: Term[_]](val term: T) {
  val nodes = new ArrayBuffer[Node[T]] with RandomDrawable[Node[T]]
}

sealed trait FactorGraphEvent;
case class AddFactorEvent(factor: Factor[_]) extends FactorGraphEvent;
case class AddNodeEvent(Node: Node[_]) extends FactorGraphEvent;

/**
 * @author Sebastian Riedel
 */
class FactorGraph[T <: Term[_]](terms: Seq[T])
        extends ListenerManager[FactorGraphEvent] {

  private val _factors = new ArrayBuffer[Factor[T]] with RandomDrawable[Factor[T]]
  private val _nodes = new ArrayBuffer[Node[T]] with RandomDrawable[Node[T]]
  private val _term2Factor = new HashMap[T, Factor[T]]
  private val _variable2Node = new HashMap[EnvVar[_], Node[T]]

  def factors: RandomDrawable[Factor[T]] = _factors

  def nodes: RandomDrawable[Node[T]] = _nodes

  for (t <- terms) {
    addTerm(t);
  }

  def addTerm(t: T): Factor[T] = {
    var factorAdded = false;
    val factor = _term2Factor.getOrElseUpdate(t,
      {val f = new Factor(t); _factors += f; factorAdded = true; f})
    for (v <- t.variables) {
      var nodeAdded = false;
      val node = _variable2Node.getOrElseUpdate(v,
        {val n = new Node[T](v); _nodes += n; nodeAdded=true; n})
      node.factors += factor
      factor.nodes += node
      if (nodeAdded) fireEvent(AddNodeEvent(node))

    }
    if (factorAdded) fireEvent(AddFactorEvent(factor))
    factor
  }

}


class DoubleFactorGraph(terms:Seq[DoubleTerm]) extends FactorGraph[DoubleTerm](terms) {

  def sum(env:Env) = SumHelper.sum(factors.map(f=>f.term),env)
}

