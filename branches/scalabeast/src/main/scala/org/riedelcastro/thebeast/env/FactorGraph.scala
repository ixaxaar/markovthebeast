package org.riedelcastro.thebeast.env

import collection.mutable.{HashMap, ArrayBuffer}
import doubles.{DoubleConstant, DoubleTerm, SumHelper}
import env._
import util.{ListenerManager, RandomDrawable}


trait FactorGraph extends ListenerManager{
  type TermType <: Term[_]
  type NodeType <: Node
  type FactorType <: Factor

  private val _factors = new ArrayBuffer[FactorType] with RandomDrawable[FactorType]
  private val _nodes = new ArrayBuffer[NodeType] with RandomDrawable[NodeType]
  private val _term2Factor = new HashMap[TermType, FactorType]
  private val _variable2Node = new HashMap[EnvVar[_], NodeType]

  def factors: RandomDrawable[FactorType] = _factors

  def nodes: RandomDrawable[NodeType] = _nodes
  

  case class Factor(val term:TermType){
    val nodes = new ArrayBuffer[NodeType] with RandomDrawable[NodeType]    
  }
  case class Node(val variable: EnvVar[_]){
    val factors = new ArrayBuffer[FactorType] with RandomDrawable[FactorType]    
  }

  sealed trait Event
  case class AddNodeEvent(node:NodeType) extends Event
  case class AddFactorEvent(factor:FactorType) extends Event

  type EventType = Event

  protected def createFactor(term:TermType) : FactorType
  protected def createNode(variable:EnvVar[_]) : NodeType

  def addTerm(t: TermType): FactorType = {
    var factorAdded = false;
    val factor = _term2Factor.getOrElseUpdate(t,
      {val f = createFactor(t); _factors += f; factorAdded = true; f})
    for (v <- t.variables) {
      var nodeAdded = false;
      val node = _variable2Node.getOrElseUpdate(v,
        {val n = createNode(v); _nodes += n; nodeAdded=true; n})
      node.factors += factor
      factor.nodes += node
      if (nodeAdded) fireEvent(AddNodeEvent(node))

    }
    if (factorAdded) fireEvent(AddFactorEvent(factor))
    factor
  }

  def addTerms(terms:Iterable[TermType]) = for (t <- terms) addTerm(t)

}

trait DoubleFactorGraph extends FactorGraph {

  type TermType = DoubleTerm
  
  def sum(env:Env) = SumHelper.sum(factors.map(f=>f.term),env)
  
}

class TestFactorGraph extends DoubleFactorGraph {
  type FactorType = Factor
  type NodeType = Node

  protected def createFactor(term: TermType) = new Factor(term)
  protected def createNode(variable: EnvVar[_]) = new Node(variable)
  
}

object TestNewFG extends Application {

  val fg = new TestFactorGraph
  fg.addListener(e => e match {
    case fg.AddFactorEvent(factor) => println(factor)
    case _ => println("Don't know")
  })
  fg.addTerm(new DoubleConstant(1.0))
  

}