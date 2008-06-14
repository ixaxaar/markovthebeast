package thebeast.pml.formula;

import thebeast.pml.formula.FormulaPrinter;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:20:13
 */
public abstract class BooleanFormula {
  public abstract void acceptBooleanFormulaVisitor(BooleanFormulaVisitor visitor);

  public String toString(){
    return new FormulaPrinter(this).getResult();
  }

}
