package thebeast.pml.solve;

import thebeast.pml.formula.FactorFormula;
import thebeast.pml.*;
import thebeast.util.Profilable;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public interface PropositionalModel extends HasProperties, Profilable {
  void init(Scores scores);

  void solve(GroundAtoms solution);

  boolean isFractional();

  void update(GroundFormulas formulas, GroundAtoms atoms);

  void update(GroundFormulas formulas, GroundAtoms atoms, Collection<FactorFormula> factors);

  boolean changed();

  void setClosure(GroundAtoms closure);

  void enforceIntegerSolution();

  void configure(Model model, Weights weights);
  
}
