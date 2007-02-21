package thebeast.pml.term;

/**
 * @author Sebastian Riedel
 */
public abstract class TermDepthFirstVisitor implements TermVisitor {
  public void visitVariable(Variable variable) {

  }

  public void visitFunctionApplication(FunctionApplication functionApplication) {
    for (Term term : functionApplication.getArguments())
      term.acceptTermVisitor(this);
  }

  public void visitIntConstant(IntConstant intConstant) {

  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {

  }

  public void visitDontCare(DontCare dontCare) {

  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {

  }

  public void visitBinnedInt(BinnedInt binnedInt) {
    binnedInt.getArgument().acceptTermVisitor(this);
  }
}
