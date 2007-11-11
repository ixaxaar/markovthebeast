package thebeast.pml.function;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 16:29:41
 */
public class DoubleCast extends Function {

  public static final DoubleCast CAST = new DoubleCast();

  protected DoubleCast() {
    super("double",Type.DOUBLE, Type.INT);
  }

  public void acceptFunctionVisitor(FunctionVisitor visitor) {
    visitor.visitDoubleCast(this);
  }
}
