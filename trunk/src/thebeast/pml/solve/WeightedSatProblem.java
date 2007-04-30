package thebeast.pml.solve;

import thebeast.pml.*;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.CNF;
import thebeast.util.Profiler;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public class WeightedSatProblem implements PropositionalModel {

  private class Clause {
    CNF cnf;
    Variable[] variables;
  }

  private class Variable {

  }

  public void init(Scores scores) {

  }

  public void solve(GroundAtoms solution) {

  }

  public boolean isFractional() {
    return false;
  }

  public void update(GroundFormulas formulas, GroundAtoms atoms) {

  }

  public void update(GroundFormulas formulas, GroundAtoms atoms, Collection<FactorFormula> factors) {

  }

  public boolean changed() {
    return false;
  }

  public void setClosure(GroundAtoms closure) {

  }

  public void enforceIntegerSolution() {

  }

  public void configure(Model model, Weights weights) {

  }

  public void setProperty(PropertyName name, Object value) {

  }

  public Object getProperty(PropertyName name) {
    return null;
  }

  public void setProfiler(Profiler profiler) {

  }
}
