package thebeast.pml.term;

import thebeast.nod.expression.ScalarExpression;
import thebeast.nod.type.IntType;
import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:55:25
 */
public class IntConstant extends Constant {

  private int integer;

  public final static IntConstant ZERO = new IntConstant(0);
  public final static IntConstant ONE = new IntConstant(1);  
  public final static IntConstant MAX_VALUE = new IntConstant(Integer.MAX_VALUE);
  public final static IntConstant MIN_VALUE = new IntConstant(Integer.MIN_VALUE);


  public IntConstant(int integer){
    this(Type.INT,integer);
  }


  public IntConstant(Type type, int integer) {
    super(type);
    this.integer = integer;
  }

  public void acceptTermVisitor(TermVisitor visitor) {
    visitor.visitIntConstant(this);
  }

  public int getInteger() {
    return integer;
  }

  public ScalarExpression toScalar() {
    return factory.createIntConstant((IntType) getType().getNodType(),integer);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    IntConstant that = (IntConstant) o;

    return integer == that.integer;

  }


  public int asInt() {
    return integer;
  }

  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + integer;
    return result;
  }


  public String toString() {
    return String.valueOf(integer);
  }

  public boolean isNonPositive() {
    return integer <= 0;
  }

  public boolean isNonNegative() {
    return integer >= 0;
  }
}
