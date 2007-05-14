package thebeast.pml.solve.weightedsat;

/**
 * @author Sebastian Riedel
 */
public interface WeightedSatSolver {

  void init();

  void addAtoms(boolean states[], double[] scores);

  void addClauses(WeightedSatClause ... clausesToAdd);

  boolean[] solve();
}
