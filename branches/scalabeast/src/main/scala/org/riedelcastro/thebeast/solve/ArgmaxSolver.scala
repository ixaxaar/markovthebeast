package org.riedelcastro.thebeast.solve


import env.{Term, Env}

/**
 * @author Sebastian Riedel
 */

trait ArgmaxSolver {

  def argmax(term:Term[Double]) : ArgmaxResult
}

class ArgmaxResult(val result:Env) {

}