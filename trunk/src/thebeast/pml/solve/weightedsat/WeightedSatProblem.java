package thebeast.pml.solve.weightedsat;

import thebeast.pml.*;
import thebeast.pml.solve.PropositionalModel;
import thebeast.pml.term.Constant;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.CNF;
import thebeast.util.Profiler;
import thebeast.nod.variable.Variable;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class WeightedSatProblem implements PropositionalModel {

  private Model model;

  public static class Grounding {
    private HashMap<Variable, Constant> mapping;
    public CNF cnf;
  }

  public static class Clause {
    public ArrayList<Node> nodes;
    public boolean state;
    public double score;
  }

  public static class Node {
    public ArrayList<Clause> clauses;
    public boolean state;
  }

  public void init(Scores scores) {

  }

  public void solve(GroundAtoms solution) {

  }

  public boolean isFractional() {
    return false;
  }

  public void update(GroundFormulas formulas, GroundAtoms atoms) {
    update(formulas,atoms,model.getFactorFormulas());
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
