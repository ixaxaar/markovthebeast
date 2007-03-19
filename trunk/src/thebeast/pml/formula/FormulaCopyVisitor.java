package thebeast.pml.formula;

import thebeast.pml.term.*;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class FormulaCopyVisitor implements BooleanFormulaVisitor, TermVisitor {

  protected BooleanFormula formula;
  protected Term term;

  public void visitAtom(Atom atom) {
    atom.acceptAtomVisitor(new AtomVisitor() {
      public void visitPredicateAtom(PredicateAtom atom) {
        LinkedList<Term> args = new LinkedList<Term>();
        for (Term arg : atom.getArguments()) {
          arg.acceptTermVisitor(FormulaCopyVisitor.this);
          args.add(term);
        }
        formula = new PredicateAtom(atom.getPredicate(), args);
      }

      public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
        cardinalityConstraint.getFormula().acceptBooleanFormulaVisitor(FormulaCopyVisitor.this);
        formula = new CardinalityConstraint(
                cardinalityConstraint.getLowerBound(),
                cardinalityConstraint.getQuantification(),
                formula, 
                cardinalityConstraint.getUpperBound(),cardinalityConstraint.useClosure());
      }
    });
  }

  public BooleanFormula getFormula() {
    return formula;
  }

  public void visitConjunction(Conjunction conjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    for (BooleanFormula formula : conjunction.getArguments()){
      formula.acceptBooleanFormulaVisitor(this);
      args.add(this.formula);
    }
    formula = new Conjunction(args);
  }

  public void visitDisjunction(Disjunction disjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    for (BooleanFormula formula : disjunction.getArguments()){
      formula.acceptBooleanFormulaVisitor(this);
      args.add(this.formula);
    }
    formula = new Disjunction(args);
  }

  public void visitImplication(Implication implication) {
    implication.getPremise().acceptBooleanFormulaVisitor(this);
    BooleanFormula premise = formula;
    implication.getConclusion().acceptBooleanFormulaVisitor(this);
    BooleanFormula conclusion = formula;
    formula = new Implication(premise, conclusion);
  }

  public void visitNot(Not not) {
    not.getArgument().acceptBooleanFormulaVisitor(this);
    formula = new Not(formula);
  }

  public void visitAcyclicityConstraint(AcyclicityConstraint acyclicityConstraint) {
    formula = acyclicityConstraint;
  }

  public void visitVariable(Variable variable) {
    term = variable;
  }

  public void visitFunctionApplication(FunctionApplication functionApplication) {
    LinkedList<Term> args = new LinkedList<Term>();
    for (Term term : functionApplication.getArguments()){
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
