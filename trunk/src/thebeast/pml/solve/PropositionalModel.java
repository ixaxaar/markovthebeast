package thebeast.pml.solve;

import thebeast.pml.formula.FactorFormula;
import thebeast.pml.*;
import thebeast.util.Profilable;

import java.util.Collection;

/**
 * A PropositionalModel implements a Ground Markov Network. It can be incrementally instantiated.
 *
 * @author Sebastian Riedel
 */
public interface PropositionalModel extends HasProperties, Profilable {

  /**
   * Resets the propositional model and sets the score table for ground atoms.
   *
   * @param scores scores of the ground atoms.
   */
  void init(Scores scores);

  /**
   * Use the scores to build a model for all local factors.
   */
  void buildLocalModel();

  /**
   * Solve the current model and update the provided solution.
   *
   * @param solution the object to write the solution to. If it already contains
   *                 ground atoms the false atoms (according to the solvers solution) will be removed
   *                 and the true ones will be added (if not already in the solution).
   */
  void solve(GroundAtoms solution);

  /**
   * Checks whether the last solution provided returned fractional values.
   *
   * @return true if the last solution provided returned fractional values.
   */
  boolean isFractional();

  void update(GroundFormulas formulas, GroundAtoms atoms);

  void update(GroundFormulas formulas, GroundAtoms atoms, Collection<FactorFormula> factors);

  /**
   * Checks whether the model has been extended in the last call to
   * {@link thebeast.pml.solve.PropositionalModel#update(thebeast.pml.GroundFormulas,thebeast.pml.GroundAtoms)}
   *
   * @return true if the model has been changed during the last update.
   */
  boolean changed();

  /**
   * Force the model to produce integer solution.
   */
  void enforceIntegerSolution();

  void setFullyGround(FactorFormula formula, boolean fullyGround);

  void setClosure(GroundAtoms closure);


  void configure(Model model, Weights weights);

}
