package org.riedelcastro.thebeast.solve


import env.{Term, Env}

/**
 * @author Sebastian Riedel
 */

trait ArgmaxSolver {

  def argmax(term:Term[Double]) : ArgmaxResult

  object Status extends Enumeration {
    type Status = Value
    val Solved, CantDo, Infeasible = Value
  }

  import Status._

  case class ArgmaxResult(val result:Env, val status:Status) {

  }
}

