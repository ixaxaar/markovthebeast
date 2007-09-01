package thebeast.pml.solve.gmn;

import thebeast.pml.solve.PropositionalModel;
import thebeast.pml.*;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.DNF;
import thebeast.util.Profiler;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.value.TupleValue;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class GroundMarkovNetwork implements PropositionalModel {

  private HashMap<GroundFormula, DNF> dnfs = new HashMap<GroundFormula, DNF>();
  


  public void init(Scores scores) {
    
  }

  public void buildLocalModel() {

  }

  public void solve(GroundAtoms solution) {

  }

  public boolean isFractional() {
    return false;
  }

  public void update(GroundFormulas formulas, GroundAtoms atoms) {

  }

  public void update(GroundFormulas formulas, GroundAtoms atoms, Collection<FactorFormula> factors) {
    //iterate over ground formulas, create new factors + corresponding nodes if not yet added (also
    //add local factors for each new node
    for (FactorFormula formula : factors){
      for (TupleValue tuple : formulas.getAllGroundFormulas(formula).value()){
        //
      }
    }
  }

  public boolean changed() {
    return false;
  }

  public void enforceIntegerSolution() {

  }

  public void setFullyGround(FactorFormula formula, boolean fullyGround) {

  }

  public void setClosure(GroundAtoms closure) {

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
