package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntAdd;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.type.IntType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemIntAdd extends AbstractMemExpression<IntType> implements IntAdd {

  private IntExpression leftHandSide, rightHandSide;

  public MemIntAdd(IntType type, IntExpression leftHandSide, IntExpression rightHandSide) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntAdd(this);
  }

  public IntExpression leftHandSide() {
    return leftHandSide;
  }

  public IntExpression rightHandSide() {
    return rightHandSide;
  }

}
