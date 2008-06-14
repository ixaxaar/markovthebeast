package thebeast.pml.term;

import thebeast.pml.Type;
import thebeast.nod.expression.ScalarExpression;
import thebeast.nod.type.IntType;
import thebeast.nod.type.BoolType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:55:25
 */
public class BoolConstant extends Constant {

  private boolean bool;


  public BoolConstant(boolean bool){
    this(Type.BOOL,bool);
  }


  public BoolConstant(Type type, boolean bool) {
    super(type);
    this.bool = bool;
  }

  public void acceptTermVisitor(TermVisitor visitor) {
    visitor.visitBoolConstant(this);
  }


  public boolean getBool() {
    return bool;
  }

  public ScalarExpression toScalar() {
    return factory.createBoolConstant(bool);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    BoolConstant that = (BoolConstant) o;

    return bool == that.bool;

  }


  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (bool ? 1 : 0);
    return result;
  }

  public String toString() {
    return String.valueOf(bool);
  }

  public boolean isNonPositive() {
    return false;
  }

  public boolean isNonNegative() {
    return false;
  }
}
