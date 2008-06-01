package thebeast.pml.formula;


import thebeast.pml.term.Variable;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 14-Feb-2007 Time: 19:47:33
 */
public class UnresolvedVariableCollector extends FormulaDepthFirstVisitor{

  private HashSet<Variable>
          bound = new HashSet<Variable>(),
          resolved = new HashSet<Variable>(),
          unresolved = new HashSet<Variable>();



  public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
    cardinalityConstraint.getLowerBound().acceptTermVisitor(this);
    cardinalityConstraint.getUpperBound().acceptTermVisitor(this);
    HashSet<Variable> oldBound = bound;
    bound = new HashSet<Variable>(bound);
    for (Variable var : cardinalityConstraint.getQuantification().getVariables())
      bound.add(var);
    cardinalityConstraint.getFormula().acceptBooleanFormulaVisitor(this);
    bound = oldBound;
  }

  public Set<Variable> getResolved() {
    return resolved;
  }

  public Set<Variable> getUnresolved() {
    return unresolved;
  }

  public void visitVariable(Variable variable) {
    if (!bound.contains(variable))
      unresolved.add(variable);
    else
      resolved.add(variable);
  }

  public void bind(Collection<Variable> variables){
    bound.addAll(variables);
  }

}
