package org.riedelcastro.thebeast.solver

/**
 * @author Sebastian Riedel
 */
class GeneralizedMaxWalkSAT extends MAPSolver {
  private[this] val rand = new java.util.Random(0)
  var maxFlips = 1000
  var greedyProb = 0.5

  def solve(problem: MAPProblem) = {
    //only process Fold(Plus,Scorers,_)
    problem.scorer match {
      case Fold(Plus, scorers, _) => solve(scorers, problem.observation)
      case _ => MAPResult.CantDo
    }
  }

  def solve(scorers: Seq[Scorer[Score]], obs: PartiallyObservedWorld): MAPResult = {
    //create initial mutable world
    val y = new MutableWorld
    //maintain list of scorers not maximal for current world
    var unsatisfied = scorers.filter(s => s.score(y) < s.max)

    //now start flipping
    for (i <- 0 to maxFlips) {
      //pick oldscorer randomly
      val scorer = unsatisfied(rand.nextInt(unsatisfied.length))
      //pick node to flip
      val node = if (rand.nextDouble > greedyProb)
        null
      else
        scorer.domain.toSeq(rand.nextInt(scorer.domain.toSeq.size));
      //flip
      //y += (node->node.f.range.toSeq(rand.nextInt(node.f.range.size)

    }
    MAPResult(y,DoubleScore(0.0),Status.Solved)
  }
}