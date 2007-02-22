package thebeast.pml;

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
}
