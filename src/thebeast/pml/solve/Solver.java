package thebeast.pml.solve;

import thebeast.util.Profiler;
import thebeast.pml.*;

import java.util.List;

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

  List<GroundAtoms> getCandidateAtoms();

  List<GroundFormulas> getCandidateFormulas();

  void setProfiler(Profiler profiler);
}
