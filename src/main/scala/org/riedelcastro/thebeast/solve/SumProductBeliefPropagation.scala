package org.riedelcastro.thebeast.solve


import env._
import doubles.{Multiplication, DoubleTerm}

/**
 * @author Sebastian Riedel
 */

class SumProductBeliefPropagation extends MarginalInference {
  class SPBPFactorGraph extends DoubleFactorGraph {
    case class SPBPEdge(override val node: NodeType, override val factor: FactorType) extends Edge(node, factor) {
      var node2factor: Belief[Any] = Ignorance(node.variable.values)
      var factor2node: Belief[Any] = Ignorance(node.variable.values)

      def updateNode2Factor = {
        node2factor = (node.belief / factor2node).normalize
      }

    }

    case class SPBPNode(override val variable: EnvVar[Any]) extends Node(variable) {
      var belief: Belief[Any] = Ignorance(variable.values)

      def updateBelief = {
        val old = belief
        belief = edges.foldLeft[Belief[Any]](Ignorance(variable.values)) {
          (r, e) => r * e.factor2node
        }.normalize
        (old - belief).norm
      }

    }

    case class SPBPFactor(override val term: TermType) extends Factor(term) {
      def updateOutgoingMessages = {
        val incomingBeliefs = new MutableBeliefs
        for (edge <- edges) incomingBeliefs.setBelief(edge.node.variable, edge.node2factor)
        val outgoingBeliefs = term.marginalize(incomingBeliefs)
        for (edge <- edges) edge.factor2node = (outgoingBeliefs.belief(edge.node.variable) / edge.node2factor).normalize
      }
    }



    type FactorType = SPBPFactor
    type NodeType = SPBPNode
    type EdgeType = SPBPEdge

    protected def createFactor(term: TermType) = SPBPFactor(term)

    protected def createNode(variable: EnvVar[_]) = SPBPNode(variable)

    protected def createEdge(node: NodeType, factor: FactorType) = SPBPEdge(node, factor)

    def updateMessages() : Double = {
      //synchronous edge processing
      for (factor <- factors)
        factor.updateOutgoingMessages

      var maxChange = 0.0
      for (node <- nodes)
        maxChange = Math.max(node.updateBelief, maxChange)

      for (edge <- edges)
        edge.updateNode2Factor

      maxChange
    }
  }




  def infer(term: DoubleTerm) = {
    val graph = new SPBPFactorGraph
    term match {
      case Multiplication(args) => graph.addTerms(args)
      case _ => graph.addTerm(term)
    }

    println(graph.factors)

    while (graph.updateMessages > 0.0001) {} 
    //graph.updateMessages

    val result = new MutableBeliefs
    for (node <- graph.nodes)
      result.setBelief(node.variable, node.belief)

    result
  }
}