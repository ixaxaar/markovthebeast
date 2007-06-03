package thebeast.pml.solve.weightedsat;

import thebeast.pml.HasProperties;

/**
 * @author Sebastian Riedel
 */
public interface WeightedSatSolver extends HasProperties {

  void init();

  void addAtoms(double[] scores);

  void addClauses(WeightedSatClause ... clausesToAdd);

  boolean[] solve();

  void setStates(boolean[] states);
}
