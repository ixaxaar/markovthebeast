package thebeast.pml.function;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 16:29:41
 */
public class DoubleProduct extends Function {

  public static final DoubleProduct PRODUCT = new DoubleProduct();

  protected DoubleProduct() {
    super("times", Type.DOUBLE, Type.DOUBLE, Type.DOUBLE);
  }

  public void acceptFunctionVisitor(FunctionVisitor visitor) {
    visitor.visitDoubleProduct(this);
  }
}
