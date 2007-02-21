package thebeast.pml;

import thebeast.pml.formula.FactorFormula;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 05-Feb-2007 Time: 16:23:12
 */
public class UnsupportedFormulaException extends RuntimeException {

  private FactorFormula formula;

  public UnsupportedFormulaException(String message, FactorFormula formula) {
    super(message + "[" + formula.toString() + "]");
    this.formula = formula;
  }

  public FactorFormula getFormula() {
    return formula;
  }
}
