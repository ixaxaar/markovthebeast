package thebeast.pml.predicate;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 31-Jan-2007 Time: 18:45:14
 */
public class DoubleLEQ extends Predicate {

  public final static DoubleLEQ DOUBLE_LEQ = new DoubleLEQ();

  protected DoubleLEQ() {
    super("dleq", Type.DOUBLE, Type.DOUBLE);
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitDoubleLEQ(this);
  }
}
