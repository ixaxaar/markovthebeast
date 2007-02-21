package thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public interface Solver {
  void configure(Model model, Weights weights);

  void setObservation(GroundAtoms atoms);

  void setScores(Scores scores);

  void solve();

  GroundAtoms getAtoms();

  GroundFormulas getFormulas();
}
