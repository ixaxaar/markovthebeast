package thebeast.pml.solve.weightedsat;

import thebeast.pml.HasProperties;
import thebeast.util.Profiler;

/**
 * @author Sebastian Riedel
 */
public interface WeightedSatSolver extends HasProperties {

  void init();

  void addAtoms(double[] scores);

  void addClauses(WeightedSatClause ... clausesToAdd);

  boolean[] solve();

  void setStates(boolean[] states);

  void setProfiler(Profiler profiler);
}
