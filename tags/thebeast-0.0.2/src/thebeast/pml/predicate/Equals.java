package thebeast.pml.predicate;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 12:38:00
 */
public class Equals extends Predicate {


  /**
   * Creates a new predicate with the given name and argument types.
   *
   * @param name the name of the predicate.
   * @param t    the type of the objects to compare.
   */
  public Equals(String name, Type t) {
    super(name, t, t);
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitEquals(this);
  }


}
