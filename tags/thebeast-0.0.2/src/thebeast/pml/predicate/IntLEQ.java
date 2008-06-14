package thebeast.pml.predicate;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 31-Jan-2007 Time: 18:45:14
 */
public class IntLEQ extends Predicate {

  public final static IntLEQ INT_LEQ = new IntLEQ();

  protected IntLEQ() {
    super("leq", Type.INT, Type.INT);
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitIntLEQ(this);
  }
}
