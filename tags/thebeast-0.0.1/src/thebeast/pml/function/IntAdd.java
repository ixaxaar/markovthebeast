package thebeast.pml.function;

import thebeast.pml.function.Function;
import thebeast.pml.Type;
import thebeast.pml.function.FunctionVisitor;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 16:29:41
 */
public class IntAdd extends Function {

  public static final IntAdd ADD = new IntAdd();

  protected IntAdd() {
    super("add", Type.INT, Type.INT, Type.INT);
  }

  public void acceptFunctionVisitor(FunctionVisitor visitor) {
    visitor.visitIntAdd(this);
  }
}
