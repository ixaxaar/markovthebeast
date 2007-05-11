package thebeast.pml.formula;

import thebeast.pml.term.*;

import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public class FormulaResolver extends FormulaCopyVisitor {

  private TermResolver termResolver = new TermResolver();

  private Map<Variable, Term> var2term;

  private static class UnresolvedVariableException extends RuntimeException {

    public UnresolvedVariableException() {
      super("One or more variables couldn't be resolved");
    }
  }

  public BooleanFormula resolve(BooleanFormula formula, Map<Variable, Term> var2term) {

    this.var2term = var2term;
    try {
      formula.acceptBooleanFormulaVisitor(this);
    } catch (UnresolvedVariableException ex) {
      return null;
    }
    return this.formula;
  }

  public void visitAtom(Atom atom) {
    atom.acceptAtomVisitor(new AtomVisitor() {
      public void visitPredicateAtom(PredicateAtom atom) {
        LinkedList<Term> args = new LinkedList<Term>();
        for (Term arg : atom.getArguments()) {
          arg.acceptTermVisitor(FormulaResolver.this);
          args.add(term);
        }
        formula = new PredicateAtom(atom.getPredicate(), args);
      }

      public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
        Map<Variable,Term> old = var2term;
        var2term = new HashMap<Variable, Term>(var2term);
        for (Variable var : cardinalityConstraint.getQuantification().getVariables())
          var2term.put(var,var);
        cardinalityConstraint.getFormula().acceptBooleanFormulaVisitor(FormulaResolver.this);
        formula = new CardinalityConstraint(
                cardinalityConstraint.getLowerBound(),
                cardinalityConstraint.getQuantification(),
                formula,
                cardinalityConstraint.getUpperBound(),cardinalityConstraint.useClosure());
        var2term = old;
      }

      public void visitTrue(True aTrue) {

      }
    });
  }
  public void visitIntConstant(IntConstant intConstant) {
    term = termResolver.resolve(intConstant, var2term);
    if (!termResolver.allResolved()) throw new UnresolvedVariableException();
  }

  public void visitFunctionApplication(FunctionApplication functionApplication) {
    term = termResolver.resolve(functionApplication, var2term);
    if (!termResolver.allResolved()) throw new UnresolvedVariableException();
  }

  public void visitDontCare(DontCare dontCare) {
    term = termResolver.resolve(dontCare, var2term);
    if (!termResolver.allResolved()) throw new UnresolvedVariableException();
  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    term = termResolver.resolve(categoricalConstant, var2term);
    if (!termResolver.allResolved()) throw new UnresolvedVariableException();
  }

  public void visitVariable(Variable variable) {
    term = termResolver.resolve(variable, var2term);
    if (!termResolver.allResolved()) throw new UnresolvedVariableException();
  }
}
