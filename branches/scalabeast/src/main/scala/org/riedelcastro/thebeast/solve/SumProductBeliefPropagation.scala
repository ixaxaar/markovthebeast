package org.riedelcastro.thebeast.solve


import collection.mutable.ArrayBuffer
import env.doubles.{Sum, DoubleTerm}
import env.{Ignorance, Belief, EnvVar, DoubleFactorGraph}
/**
 * @author Sebastian Riedel
 */

class SumProductBeliefPropagation extends MarginalInference {

  class SPBPFactorGraph extends DoubleFactorGraph {
    case class SPBPFactor(override val term:TermType) extends Factor(term){
    
    }
    case class SPBPNode(envVar:EnvVar[_]) extends Node(envVar) {

    }

    type FactorType = SPBPFactor
    type NodeType = SPBPNode
    type EdgeType = Edge

    protected def createFactor(term: TermType) = SPBPFactor(term)
    protected def createNode(variable: EnvVar[_]) = SPBPNode(variable)

    protected def createEdge(node: NodeType, factor: FactorType) = Edge(node,factor)
  }


  def infer(term: DoubleTerm) = {
    val graph = new SPBPFactorGraph
    term match {
      case Sum(args) => graph.addTerms(args)
      case _ => graph.addTerm(term)
    }
    //go through all factors, take the belief of the neigbouring nodes, divide by previous message from
    //factor to node, pass to factor, and calculate new beliefs over variables through marginalize
    //method of term
    null
  }
}