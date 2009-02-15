package org.riedelcastro.thebeast.solver
/**
 * @author Sebastian Riedel
 */

class GeneralizedMaxWalkSAT extends MAPSolver {
  def solve(problem: MAPProblem) = {
    //only process Fold(Plus,Scorers,_)
    problem.scorer match {
      case Fold(Plus,scorers,_) => solve(scorers, problem.observation)
      case x => solve(Seq(x),problem.observation)
    }
  }

  def solve(scorers:Seq[Scorer[Score]], obs:PartiallyObservedWorld) : MAPResult = {
    //create initial mutable world
    val y = new MutableWorld
    //maintain list of scorers not maximal for current world
    val unsatisfied = scorers.filter(s => s.score(y) < s.max)
    //pick scorer randomly
    //either:
    // - for each node in domain of scorer calculate global score delta and change node with highest delta
    // - pick random node in domain and change randomly (however, not to old value)
    MAPResult.CantDo
  }
}