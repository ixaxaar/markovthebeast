package com.googlecode.thebeast.inference;

import com.googlecode.thebeast.pml.Assignment;
import com.googlecode.thebeast.pml.GroundMarkovNetwork;
import com.googlecode.thebeast.pml.PMLVector;

/**
 * @author Sebastian Riedel
 */
public class ExhaustiveMAPSolver {

  private GroundMarkovNetwork gmn;
  private PMLVector weights;

  public ExhaustiveMAPSolver(GroundMarkovNetwork gmn, PMLVector weights) {
    this.gmn = gmn;
    this.weights = weights;
  }

  public Assignment solve(){
    return new Assignment(gmn);
  }

}
