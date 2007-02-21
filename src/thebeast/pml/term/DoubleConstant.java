package thebeast.pml.term;

import thebeast.pml.Type;
import thebeast.nod.expression.ScalarExpression;
import thebeast.nod.type.IntType;
import thebeast.nod.type.DoubleType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:55:25
 */
public class DoubleConstant extends Constant {

  private double value;


  public DoubleConstant(double value){
    this(Type.DOUBLE,value);
  }

  public DoubleConstant(Type type, double value) {
    super(type);
    this.value = value;
  }

  public void acceptTermVisitor(TermVisitor visitor) {
    visitor.visitDoubleConstant(this);
  }

  public double getValue() {
    return value;
  }

  public ScalarExpression toScalar() {
    return factory.createDoubleConstant((DoubleType) getType().getNodType(), value);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    DoubleConstant that = (DoubleConstant) o;

    return value == that.value;

  }

  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (int) Math.round(value);
    return result;
  }


  public String toString() {
    return String.valueOf(value);
  }

  public boolean isNonPositive() {
    return value <= 0;
  }

  public boolean isNonNegative() {
    return value >= 0;
  }
}
