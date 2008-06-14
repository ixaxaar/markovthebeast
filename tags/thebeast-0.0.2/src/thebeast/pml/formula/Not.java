package thebeast.pml.formula;

/**
 * @author Sebastian Riedel
 */
public class Not extends BooleanFormula{

  private BooleanFormula argument;

  public Not(BooleanFormula argument) {
    this.argument = argument;
  }

  public BooleanFormula getArgument() {
    return argument;
  }

  public void acceptBooleanFormulaVisitor(BooleanFormulaVisitor visitor) {
    visitor.visitNot(this);
  }
}
