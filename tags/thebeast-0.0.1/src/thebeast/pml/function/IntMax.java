package thebeast.pml.function;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 16:29:41
 */
public class IntMax extends Function {

  public static final IntMax MAX = new IntMax();

  protected IntMax() {
    super("max", Type.INT, Type.INT, Type.INT);
  }

  public void acceptFunctionVisitor(FunctionVisitor visitor) {
    visitor.visitIntMax(this);
  }
}
