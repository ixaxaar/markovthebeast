package thebeast.pml.function;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 16:29:41
 */
public class DoubleAbs extends Function {

  public static final DoubleAbs ABS = new DoubleAbs();

  protected DoubleAbs() {
    super("abs", Type.DOUBLE, Type.DOUBLE);
  }

  public void acceptFunctionVisitor(FunctionVisitor visitor) {
    visitor.visitDoubleAbs(this);
  }
}
