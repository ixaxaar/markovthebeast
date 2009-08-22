package org.riedelcastro.thebeast.solve


import env._
import env.doubles.{Sum, DoubleTerm}

/**
 * @author Sebastian Riedel
 */

class SumProductBeliefPropagation extends MarginalInference {
  class SPBPFactorGraph extends DoubleFactorGraph {
    case class SPBPEdge(override val node: NodeType, override val factor: FactorType) extends Edge(node, factor) {
      var node2factor: Belief[Any] = Ignorance(node.variable.values)
      var factor2node: Belief[Any] = Ignorance(node.variable.values)

      def updateNode2Factor = {
        node2factor = node.belief / factor2node
      }

    }

    case class SPBPNode(override val variable: EnvVar[Any]) extends Node(variable) {
      var belief: Belief[Any] = Ignorance(variable.values)

      def updateBelief = {
        belief = edges.foldLeft[Belief[Any]](Ignorance(variable.values)) {
          (r, e) => r * e.factor2node
        }
      }

    }

    case class SPBPFactor(override val term: TermType) extends Factor(term) {
      def updateOutgoingMessages = {
        val incomingBeliefs = new MutableBeliefs
        for (edge <- edges) incomingBeliefs.setBelief(edge.node.variable, edge.node2factor)
        val outgoingBeliefs = incomingBeliefs //this should be term.marginalize(incomingBeliefs)
        for (edge <- edges) edge.factor2node = outgoingBeliefs.belief(edge.node.variable) / edge.node2factor
      }
    }



    type FactorType = SPBPFactor
    type NodeType = SPBPNode
    type EdgeType = SPBPEdge

    protected def createFactor(term: TermType) = SPBPFactor(term)

    protected def createNode(variable: EnvVar[_]) = SPBPNode(variable)

    protected def createEdge(node: NodeType, factor: FactorType) = SPBPEdge(node, factor)
  }


  def infer(term: DoubleTerm) = {
    val graph = new SPBPFactorGraph
    term match {
      case Sum(args) => graph.addTerms(args)
      case _ => graph.addTerm(term)
    }
    //synchronous edge processing

    //go through all factors, take the belief of the neigbouring nodes, divide by previous message from
    //factor to node, pass to factor, and calculate new beliefs over variables through marginalize
    //method of term
    null
  }
}