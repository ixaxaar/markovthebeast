package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntConstant;
import thebeast.nod.type.IntType;
import thebeast.nod.value.IntValue;

/**
 * @author Sebastian Riedel
 */
public class MemIntConstant extends AbstractMemConstant<IntType, IntValue> implements IntConstant {

  private int constant;

  protected MemIntConstant(IntType type, int constant) {
    super(type);
    this.constant = constant;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntConstant(this);
  }

  public int getInt() {
    return constant;
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    MemIntConstant that = (MemIntConstant) o;

    if (constant != that.constant) return false;

    return true;
  }

  public int hashCode() {
    return constant;
  }

  public String toString(){
    return String.valueOf(constant);
  }

}
