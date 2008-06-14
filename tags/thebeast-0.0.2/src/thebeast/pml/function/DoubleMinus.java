package thebeast.pml.function;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 16:29:41
 */
public class DoubleMinus extends Function {

  public static final DoubleMinus MINUS = new DoubleMinus();

  protected DoubleMinus() {
    super("dminus", Type.DOUBLE, Type.DOUBLE, Type.DOUBLE);
  }

  public void acceptFunctionVisitor(FunctionVisitor visitor) {
    visitor.visitDoubleMinus(this);
  }
}
