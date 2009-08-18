package org.riedelcastro.thebeast.solve


import env._
import doubles.{DoubleTerm, Sum}
import vectors.VectorDotApp

/**
 * @author Sebastian Riedel
 */

trait FactorGraphArgmaxSolver extends ArgmaxSolver {
  def solve(): ArgmaxResult;

  def getFactorGraph() : DoubleFactorGraph
  def createFactorGraph() : DoubleFactorGraph

  def argmax(term: DoubleTerm) = {
    term match {
      case x: Sum => {
        val graph = createFactorGraph
        graph.addTerms(x.args)
        solve();
      }
      case x: VectorDotApp => argmax(x.distribute)
      case _ => ArgmaxResult(null, Status.CantDo, 0)
    }
  }
}