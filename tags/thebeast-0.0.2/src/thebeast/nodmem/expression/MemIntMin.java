package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.expression.IntAdd;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntMin;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemIntMin extends AbstractMemExpression<IntType> implements IntMin {

  private IntExpression leftHandSide, rightHandSide;

  public MemIntMin(IntType type, IntExpression leftHandSide, IntExpression rightHandSide) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntMin(this);
  }

  public IntExpression leftHandSide() {
    return leftHandSide;
  }

  public IntExpression rightHandSide() {
    return rightHandSide;
  }

}
