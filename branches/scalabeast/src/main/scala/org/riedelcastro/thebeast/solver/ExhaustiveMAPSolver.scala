package org.riedelcastro.thebeast.solver

import org.specs._
import org.specs.runner._

/**
 * @author Sebastian Riedel
 */

class ExhaustiveMAPSolver extends MAPSolver{
  def solve(problem: MAPProblem): MAPResult = {
    Cartesian.allWorlds(problem.signature, problem.observation).foldLeft(
      MAPResult(null, Score.NEGINF, Status.Solved)){
      (result, world) => val score = problem.scorer.score(world);
      if (score > result.score) MAPResult(world, score, Status.Solved) else result
    }
  }
}

//class ExhaustiveMAPSolverTest extends JUnit4(ExhaustiveMAPSolverSpec)

object ExhaustiveMAPSolverSpec extends Specification with TheBeastApp {
  "The solver" should {
    "not return null" in {
      val f = "f" := Set(1,2) -> Set(true,false)
      val scorer = GroundAtomScorer(GroundAtom(f,1,true))
      val obs = new MutablePartiallyObservedWorld
      obs.setFunction(f, OpenWorldProxy(Map(1->true)))
      new ExhaustiveMAPSolver().solve(MAPProblem(Seq(f),obs,scorer)) must notBeNull
    }

    "return a world equivalent to a conjunction of atoms in the oldscorer if the conjunction is exhaustive" in {
      val f = "f" := Set(1,2) -> Set(true,false)
      val scorer = Apply(And, GroundAtomScorer(GroundAtom(f,1,true)), GroundAtomScorer(GroundAtom(f,2,false)))
      val obs = new MutablePartiallyObservedWorld
      obs.setFunction(f, OpenWorldProxy(Map(1->true)))
      val result = new ExhaustiveMAPSolver().solve(MAPProblem(Seq(f),obs,scorer)).world
      result.getFunction(f)(1) mustEqual true
      result.getFunction(f)(2) mustEqual false
    }
  }
}
