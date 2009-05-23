package org.riedelcastro.thebeast.solve


import collection.mutable.{ArrayBuffer, HashMap, HashSet, MultiMap}
import env._
import scala.util.Random

/**
 * @author Sebastian Riedel
 */


trait RandomDrawable[T] extends Seq[T] {
  def randomValue:T = this((new Random).nextInt(size))
}

class FactorGraph[T <: Term[_]](terms:Seq[T]) {
 
  private val _factors = new ArrayBuffer[Factor] with RandomDrawable[Factor]
  private val _nodes = new ArrayBuffer[Node] with RandomDrawable[Node]
  private val _term2Factor = new HashMap[T,Factor]
  private val _variable2Node = new HashMap[EnvVar[_],Node]

  def factors:RandomDrawable[Factor] = _factors
  def nodes:RandomDrawable[Node] = _nodes

  for (t <- terms) {
    val factor = _term2Factor.getOrElseUpdate(t, {val f = new Factor(t); _factors += f; f})
    for (v <- t.variables){
      val node = _variable2Node.getOrElseUpdate(v, {val n = new Node(v); _nodes += n; n})
      node.factors += factor
      factor.nodes += node
    }
  }

  class Node(val variable:EnvVar[_]) {
    val factors = new ArrayBuffer[Factor] with RandomDrawable[Factor]
  }

  class Factor(val term:T) {
    val nodes = new ArrayBuffer[Node] with RandomDrawable[Node]
  }
}

class GeneralizedMaxWalkSAT extends ArgmaxSolver {

  private val random = new Random

  val maxFlips = 1000

  def argmax(term: Term[Double]) = {
    //we can only do Fold(Add,...) terms
    term match {
      case x:Sum => argmaxSum(x)
      case _ => ArgmaxResult(null, Status.CantDo, 0)
    }
  }

  private def greedy(factor:FactorGraph[DoubleTerm]#Factor, y:MutableEnv) : Double = {
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

  private def randomChange(factor:FactorGraph[DoubleTerm]#Factor, y:MutableEnv) : Double = {
    val node = factor.nodes.randomValue
    val oldScore = node.factors.foldLeft(0.0) {(s,f)=>s + y(f.term)}
    y += ((node.variable, node.variable.values.randomValue))
    val newScore = node.factors.foldLeft(0.0) {(s,f)=>s + y(f.term)}    
    newScore - oldScore
  }

  private def argmaxSum(sum : Sum): ArgmaxResult = {
    val args = sum.args
    //create FactorGraph
    val graph = new FactorGraph[DoubleTerm](args)

    val y = new MutableEnv
    //generate initial solution
    for (n <- graph.nodes) y += ((n.variable,n.variable.values.randomValue))

    //score
    var max = y(sum)
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

    //pick a random term of these
    //randomly change a variable OR pick a variable that when changed results in largest increase
    ArgmaxResult(best, Status.Solved, max)
  }


}



