package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.BoolType;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.BoolValue;
import thebeast.nod.expression.IntConstant;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.BoolConstant;

/**
 * @author Sebastian Riedel
 */
public class MemBoolConstant extends AbstractMemConstant<BoolType, BoolValue> implements BoolConstant {

  private boolean constant;

  protected MemBoolConstant(BoolType type, boolean constant) {
    super(type);
    this.constant = constant;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitBoolConstant(this);
  }

  public boolean getBoolean() {
    return constant;
  }

  public int hashCode() {
    return constant ? 1 : 0;
  }
}
