package thebeast.pml.predicate;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 31-Jan-2007 Time: 18:45:14
 */
public class IntGT extends Predicate {

  public final static IntGT INT_GT = new IntGT();

  protected IntGT() {
    super("gt", Type.INT, Type.INT);
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitIntGT(this);
  }
}
