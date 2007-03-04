package thebeast.pml;

import thebeast.util.Profiler;

/**
 * @author Sebastian Riedel
 */
public interface Solver extends HasProperties {
  void configure(Model model, Weights weights);

  void setObservation(GroundAtoms atoms);

  void setScores(Scores scores);

  void solve();

  GroundAtoms getAtoms();

  GroundFormulas getFormulas();

  void setProfiler(Profiler profiler);
}
