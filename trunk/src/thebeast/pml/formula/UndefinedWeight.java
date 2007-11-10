package thebeast.pml.formula;

import thebeast.pml.term.FunctionApplication;

/**
 * @author Sebastian Riedel
 */
public class UndefinedWeight extends Atom {

  private FunctionApplication functionApplication;

  public void acceptAtomVisitor(AtomVisitor visitor) {
    visitor.visitUndefinedWeight(this);
  }

  public UndefinedWeight(FunctionApplication functionApplication) {
    this.functionApplication = functionApplication;
  }

  public FunctionApplication getFunctionApplication() {
    return functionApplication;
  }
}
