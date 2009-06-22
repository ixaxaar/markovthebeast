package org.riedelcastro.thebeast.solve


import env._
import util.{DoubleFactorGraph, Factor}
/**
 * @author Sebastian Riedel
 */

class GeneralizedMaxWalkSAT extends FactorGraphArgmaxSolver {

  private val random = new scala.util.Random
  private var graph:DoubleFactorGraph = null

  val maxFlips = 1000


  def setFactorGraph(graph: DoubleFactorGraph) = {
    this.graph = graph
    //this.graph.addListener(e => e);
  }


  private def greedy(factor:Factor[DoubleTerm], y:MutableEnv) : Double = {
    var bestChoice:(EnvVar[_],Any) = null
    var maxDelta = Math.NEG_INF_DOUBLE
    for (node <- factor.nodes){
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

  private def randomChange(factor:Factor[DoubleTerm], y:MutableEnv) : Double = {
    val node = factor.nodes.randomValue
    val oldScore = node.factors.foldLeft(0.0) {(s,f)=>s + y(f.term)}
    y += ((node.variable, node.variable.values.randomValue))
    val newScore = node.factors.foldLeft(0.0) {(s,f)=>s + y(f.term)}
    newScore - oldScore
  }

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
      score = score + (if (Math.random < 0.5) randomChange(violated, y) else greedy(violated, y))
      if (score > max){
        max = score
        best = y.clone
      }
    }

    ArgmaxResult(best, Status.Solved, max)
  }


}
