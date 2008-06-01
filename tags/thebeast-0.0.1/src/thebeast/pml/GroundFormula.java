package thebeast.pml;

import thebeast.pml.formula.FactorFormula;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class GroundFormula {

  private FactorFormula formula;
  private Assignment assignment;

  public GroundFormula(FactorFormula formula, Assignment assignment) {
    this.formula = formula;
    this.assignment = assignment;
  }


  public Assignment getAssignment() {
    return assignment;
  }

  public FactorFormula getFormula() {
    return formula;
  }
}
