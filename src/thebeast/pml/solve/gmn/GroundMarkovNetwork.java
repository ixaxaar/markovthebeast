package thebeast.pml.solve.gmn;

import thebeast.pml.solve.PropositionalModel;
import thebeast.pml.*;
import thebeast.pml.term.DoubleConstant;
import thebeast.pml.term.Constant;
import thebeast.pml.term.Variable;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.DNF;
import thebeast.util.Profiler;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.Value;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class GroundMarkovNetwork implements PropositionalModel {

  private HashMap<GroundFormula, DNF> dnfs = new HashMap<GroundFormula, DNF>();
  private Weights weights;



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
      double constantWeight = formula.getWeight() instanceof DoubleConstant ?
        ((DoubleConstant)formula.getWeight()).getValue() : 0;
      for (TupleValue tuple : formulas.getNewGroundFormulas(formula).value()){
        //create Factor
        Factor factor;
        if (formula.isDeterministic()){
          factor = new Factor(formula,constantWeight);
        } else {
          factor = new Factor(formula,weights.getWeight(tuple.intElement("index").getInt()));
        }
        for (int i = 0; i < formula.getQuantification().getVariables().size();++i){
          Variable variable = formula.getQuantification().getVariables().get(i);
          Constant constant = variable.getType().toConstant(tuple.element(formula.isDeterministic() ? i : i + 1));
          factor.addAssignment(variable, constant);
        }
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

  public int getGroundAtomCount() {
    return 0;
  }

  public int getGroundFormulaCount() {
    return 0;
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
