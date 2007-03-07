package thebeast.pml.predicate;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 31-Jan-2007 Time: 18:45:14
 */
public class IntGEQ extends Predicate {

  public final static IntGEQ INT_GEQ = new IntGEQ();

  protected IntGEQ() {
    super("geq", Type.INT, Type.INT);
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitIntGEQ(this);
  }
}
