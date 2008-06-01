package thebeast.pml.formula;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 25-Jan-2007 Time: 12:27:52
 */
public class Implication extends BooleanFormula {

  private BooleanFormula premise, conclusion;

  public Implication(BooleanFormula premise, BooleanFormula conclusion) {
    this.premise = premise;
    this.conclusion = conclusion;
  }

  public BooleanFormula getConclusion() {
    return conclusion;
  }

  public BooleanFormula getPremise() {
    return premise;
  }

  public void acceptBooleanFormulaVisitor(BooleanFormulaVisitor visitor) {
    visitor.visitImplication(this);
  }
}
