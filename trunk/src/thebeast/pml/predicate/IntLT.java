package thebeast.pml.predicate;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 31-Jan-2007 Time: 18:45:14
 */
public class IntLT extends Predicate {

  public final static IntLT INT_LT = new IntLT();

  protected IntLT() {
    super("lt", Type.INT, Type.INT);
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitIntLT(this);
  }
}
