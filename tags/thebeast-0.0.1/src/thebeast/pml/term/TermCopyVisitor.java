package thebeast.pml.term;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class TermCopyVisitor implements TermVisitor {

  protected Term term;

  public Term copy(Term term){
    term.acceptTermVisitor(this);
    return this.term;
  }

  public void visitVariable(Variable variable) {
    term = variable;
  }

  public void visitFunctionApplication(FunctionApplication functionApplication) {
    LinkedList<Term> args = new LinkedList<Term>();
    for (Term term : functionApplication.getArguments()) {
      term.acceptTermVisitor(this);
      args.add(this.term);
    }
    term = new FunctionApplication(functionApplication.getFunction(), args);
  }

  public void visitIntConstant(IntConstant intConstant) {
    term = intConstant;
  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    term = categoricalConstant;
  }

  public void visitDontCare(DontCare dontCare) {
    term = dontCare;
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    term = doubleConstant;
  }

  public void visitBinnedInt(BinnedInt binnedInt) {
    binnedInt.getArgument().acceptTermVisitor(this);
    term = new BinnedInt(binnedInt.getBins(), term);
  }

  public void visitBoolConstant(BoolConstant boolConstant) {
    term = boolConstant;
  }
}
