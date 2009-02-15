package org.riedelcastro.thebeast.solver
/**
 * @author Sebastian Riedel
 */

trait MAPSolver {
  def solve(problem:MAPProblem) : MAPResult
}