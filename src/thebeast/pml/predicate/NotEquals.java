package thebeast.pml.predicate;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 12:38:00
 */
public class NotEquals extends Predicate {


  /**
   * Creates a new predicate with the given name and argument types.
   *
   * @param name the name of the predicate.
   * @param type the type of the objects to compare.
   */
  public NotEquals(String name, Type type) {
    super(name, type, type);
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitNotEquals(this);
  }


}
