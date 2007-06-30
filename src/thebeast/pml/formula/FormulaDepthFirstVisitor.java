package thebeast.pml.formula;

import thebeast.pml.term.*;

/**
 * @author Sebastian Riedel
 */
public class FormulaDepthFirstVisitor extends AbstractAtomVisitor implements BooleanFormulaVisitor, TermVisitor {
  public void visitAtom(Atom atom) {
      atom.acceptAtomVisitor(this);
  }

  public void visitConjunction(Conjunction conjunction) {
    for (BooleanFormula formula : conjunction.getArguments())
      formula.acceptBooleanFormulaVisitor(this);
  }

  public void visitDisjunction(Disjunction disjunction) {
    for (BooleanFormula formula : disjunction.getArguments())
      formula.acceptBooleanFormulaVisitor(this);

  }

  public void visitImplication(Implication implication) {
    implication.getPremise().acceptBooleanFormulaVisitor(this);
    implication.getConclusion().acceptBooleanFormulaVisitor(this);
  }

  public void visitNot(Not not) {
    not.getArgument().acceptBooleanFormulaVisitor(this);
  }

  public void visitAcyclicityConstraint(AcyclicityConstraint acyclicityConstraint) {

  }


  public void visitVariable(Variable variable) {

  }

  public void visitFunctionApplication(FunctionApplication functionApplication) {
    for (Term arg : functionApplication.getArguments())
      arg.acceptTermVisitor(this);
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

  public void visitBoolConstant(BoolConstant boolConstant) {

  }

  public void visitPredicateAtom(PredicateAtom predicateAtom) {
    for (Term term : predicateAtom.getArguments())
      term.acceptTermVisitor(FormulaDepthFirstVisitor.this);
  }

  public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
    cardinalityConstraint.getLowerBound().acceptTermVisitor(FormulaDepthFirstVisitor.this);
    cardinalityConstraint.getUpperBound().acceptTermVisitor(FormulaDepthFirstVisitor.this);
    cardinalityConstraint.getFormula().acceptBooleanFormulaVisitor(FormulaDepthFirstVisitor.this);
  }

  public void visitTrue(True aTrue) {

  }
}
