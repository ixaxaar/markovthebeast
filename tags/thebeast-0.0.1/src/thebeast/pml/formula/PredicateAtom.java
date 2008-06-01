package thebeast.pml.formula;

import thebeast.pml.term.Term;
import thebeast.pml.term.DontCare;
import thebeast.pml.predicate.Predicate;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 05-Feb-2007 Time: 17:14:58
 */
public class PredicateAtom extends Atom{
  private List<Term> arguments;
  private Predicate predicate;


  public PredicateAtom(Predicate predicate, List<Term> arguments) {
    this.predicate = predicate;
    this.arguments = new ArrayList<Term>(arguments);
  }

  public PredicateAtom(Predicate predicate, Term ... arguments) {
    this(predicate, Arrays.asList(arguments));
  }

  public boolean containsDontCare(){
    for (Term term : arguments)
      if (term instanceof DontCare)
        return true;
    return false;
  }

  public List<Term> getArguments() {
    return arguments;
  }

  public Predicate getPredicate() {
    return predicate;
  }


  public void acceptAtomVisitor(AtomVisitor visitor) {
    visitor.visitPredicateAtom(this);
  }
}
