package thebeast.pml.term;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Riedel
 */
public class TermResolver extends TermCopyVisitor {

  private Map<Variable, Term> var2term;
  private LinkedList<Variable> resolved,unresolved;


  public Term resolve(Term term, Map<Variable,Term> var2term){
    this.var2term = var2term;
    resolved = new LinkedList<Variable>();
    unresolved = new LinkedList<Variable>();
    term.acceptTermVisitor(this);
    return this.term;
  }

  public List<Variable> getResolved() {
    return resolved;
  }

  public List<Variable> getUnresolved() {
    return unresolved;
  }

  public boolean allResolved(){
    return unresolved.size() == 0;
  }

  public void visitVariable(Variable variable) {
    Term mapped = var2term.get(variable);
    if (mapped == null){
      unresolved.add(variable);
      term = variable;
    } else {
      resolved.add(variable);
      term = mapped;
    }
  }
}
