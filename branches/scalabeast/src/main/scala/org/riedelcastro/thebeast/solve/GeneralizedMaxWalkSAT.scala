package org.riedelcastro.thebeast.solve


import env._
import doubles.DoubleTerm
/**
 * @author Sebastian Riedel
 */

class GeneralizedMaxWalkSAT extends FactorGraphArgmaxSolver {

  class MWSFactorGraph extends DoubleFactorGraph {
    type NodeType = Node
    type FactorType = MWSFactor

    protected def createFactor(term: TermType) = new MWSFactor(term)
    protected def createNode(variable: EnvVar[_]) = new Node(variable)

    class MWSFactor(term:DoubleTerm) extends Factor(term) {

      def greedy(y:MutableEnv) : Double = {
        var bestChoice:(EnvVar[_],Any) = null
        var maxDelta = Math.NEG_INF_DOUBLE
        for (node <- nodes){
          val current = y.resolveVar(node.variable).get
          val oldScore = node.factors.foldLeft(0.0) {(s,f) => s + y(f.term)}
          for (value <- node.variable.values; if value != current){
            y += ((node.variable, value))
            val newScore = node.factors.foldLeft(0.0) {(s,f) => s + y(f.term)}
            val delta = newScore - oldScore
            if (delta > maxDelta) {
              maxDelta = delta
              bestChoice = (node.variable,value)
            }
          }
          y += ((node.variable, current))
        }
        y += ((bestChoice._1, bestChoice._2))
        maxDelta
      }

      def randomChange(y:MutableEnv) : Double = {
        val node = nodes.randomValue
        val oldScore = node.factors.foldLeft(0.0) {(s,f)=>s + y(f.term)}
        y += ((node.variable, node.variable.values.randomValue))
        val newScore = node.factors.foldLeft(0.0) {(s,f)=>s + y(f.term)}
        newScore - oldScore
      }

    }

  }


  private val random = new scala.util.Random
  private var graph:MWSFactorGraph = null

  val maxFlips = 1000


  def createFactorGraph() = {
    graph = new MWSFactorGraph
    graph
  }

  def getFactorGraph() = graph

  def solve(): ArgmaxResult = {

    val y = new MutableEnv
    //generate initial solution
    for (n <- graph.nodes) y += ((n.variable,n.variable.values.randomValue))

    //score
    var max = graph.sum(y)
    var score = max
    var best = y.clone

    for (flip <- 0 until maxFlips) {
      //find all terms not at their optimum => need to know what the max of a term is
      val suboptimal = graph.factors.filter(f => y(f.term) < f.term.upperBound)
      val violated = suboptimal(random.nextInt(suboptimal.size))
      score = score + (if (Math.random < 0.5) violated.randomChange(y) else violated.greedy(y))
      if (score > max){
        max = score
        best = y.clone
      }
    }

    ArgmaxResult(best, Status.Solved, max)
  }


}
