package thebeast.pml.term;

import thebeast.pml.term.FunctionApplication;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:23:02
 */
public interface TermVisitor {

  void visitVariable(Variable variable);

  void visitFunctionApplication(FunctionApplication functionApplication);

  void visitIntConstant(IntConstant intConstant);

  void visitCategoricalConstant(CategoricalConstant categoricalConstant);

  void visitDontCare(DontCare dontCare);

  void visitDoubleConstant(DoubleConstant doubleConstant);

  void visitBinnedInt(BinnedInt binnedInt);

  void visitBoolConstant(BoolConstant boolConstant);
}
