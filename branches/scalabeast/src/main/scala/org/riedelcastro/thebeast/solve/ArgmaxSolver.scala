package org.riedelcastro.thebeast.solve


import env.doubles.DoubleTerm
import env.{Env}

/**
 * @author Sebastian Riedel
 */

trait ArgmaxSolver {

  def argmax(term:DoubleTerm) : ArgmaxResult

  object Status extends Enumeration {
    type Status = Value
    val Solved, CantDo, Infeasible = Value
  }

  import Status._

  case class ArgmaxResult(val result:Env, val status:Status, val score:Double) {

  }
}

