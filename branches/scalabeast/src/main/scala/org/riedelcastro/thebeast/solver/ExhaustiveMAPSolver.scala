package org.riedelcastro.thebeast.solver

import org.specs._
import org.specs.runner._

/**
 * @author Sebastian Riedel
 */

class ExhaustiveMAPSolver {
  def solve(problem: MAPProblem): MAPResult = {
    Cartesian.allWorlds(problem.signature, problem.observation).foldLeft(MAPResult(null, DoubleScore(-99))){
      (result, world) => val score = problem.scorer.score(world);
      if (score > result.score) MAPResult(world, score) else result
    }
  }
}

//class ExhaustiveMAPSolverTest extends JUnit4(ExhaustiveMAPSolverSpec)

object ExhaustiveMAPSolverSpec extends Specification {
  "The solver" should {
    "not return null" in {
      val f = new FunctionSymbol("f", Set(1,2), Set(true,false))
      val scorer = GroundAtomScorer(GroundAtom(f,1,true))
      val obs = new MutablePartiallyObservedWorld
      obs.setFunction(f, OpenWorldProxy(Map(1->true)))
      new ExhaustiveMAPSolver().solve(MAPProblem(Seq(f),obs,scorer)) must notBeNull
    }

    "return a world equivalent to a conjunction of atoms in the scorer if this set is exhaustive" in {
      val f = new FunctionSymbol("f", Set(1,2), Set(true,false))
      val scorer = Apply(And, GroundAtomScorer(GroundAtom(f,1,true)), GroundAtomScorer(GroundAtom(f,2,false)))
      val obs = new MutablePartiallyObservedWorld
      obs.setFunction(f, OpenWorldProxy(Map(1->true)))
      val result = new ExhaustiveMAPSolver().solve(MAPProblem(Seq(f),obs,scorer)).world
      result.getFunction(f)(1) mustEqual true
      result.getFunction(f)(2) mustEqual false
    }
  }
}
