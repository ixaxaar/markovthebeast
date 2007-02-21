package thebeast.nodmem.expression;

import thebeast.nod.expression.DoubleConstant;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.type.DoubleType;
import thebeast.nod.value.DoubleValue;

/**
 * @author Sebastian Riedel
 */
public class MemDoubleConstant extends AbstractMemConstant<DoubleType, DoubleValue> implements DoubleConstant {

  private double constant;

  protected MemDoubleConstant(DoubleType type, double constant) {
    super(type);
    this.constant = constant;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleConstant(this);
  }

  public double getDouble() {
    return constant;
  }


  public int hashCode() {
    return (int) Math.round(constant);
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemDoubleConstant that = (MemDoubleConstant) o;

    return Double.compare(that.constant, constant) == 0;

  }
}
