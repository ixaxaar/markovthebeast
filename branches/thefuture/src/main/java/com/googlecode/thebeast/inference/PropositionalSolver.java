package com.googlecode.thebeast.inference;

import com.googlecode.thebeast.pml.Assignment;

/**
 * @author Sebastian Riedel
 */
public interface PropositionalSolver {
  Assignment solve(Assignment observed);
}
