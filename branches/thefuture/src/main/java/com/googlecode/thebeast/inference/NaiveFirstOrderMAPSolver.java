package com.googlecode.thebeast.inference;

import com.googlecode.thebeast.pml.PMLVector;
import com.googlecode.thebeast.pml.PseudoMarkovLogicNetwork;
import com.googlecode.thebeast.world.World;

/**
 * @author Sebastian Riedel
 */
public class NaiveFirstOrderMAPSolver {

  private PMLVector weights;
  private PseudoMarkovLogicNetwork mln;
  private PropositionalSolver propositionalSolver;

  public NaiveFirstOrderMAPSolver(PseudoMarkovLogicNetwork mln,
                                  PMLVector weights,
                                  PropositionalSolver propositionalSolver) {
    this.mln = mln;
    this.weights = weights;
    this.propositionalSolver = propositionalSolver;
  }

  public World solve(World observation){
    World result = observation.getSignature().createWorld(observation);
    return result;
  }

}
