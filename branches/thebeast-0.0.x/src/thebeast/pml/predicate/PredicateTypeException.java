package thebeast.pml.predicate;

import thebeast.pml.term.Term;
import thebeast.pml.predicate.Predicate;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 19:01:32
 */
public class PredicateTypeException extends RuntimeException {

  private Term observed;
  private Predicate predicate;
  private int argumentIndex;

  public PredicateTypeException(Predicate predicate, int argumentIndex, Term observed) {
    super("Predicate " + predicate + " expects a " + predicate.getArgumentTypes().get(argumentIndex).getName() +
            " term in position " + argumentIndex + " but got a " + observed.getType().getName() +
            " (" + observed + ")");
    this.predicate = predicate;
    this.argumentIndex = argumentIndex;
    this.observed = observed;
  }


  public int getArgumentIndex() {
    return argumentIndex;
  }

  public Term getObserved() {
    return observed;
  }

  public Predicate getPredicate() {
    return predicate;
  }
}
