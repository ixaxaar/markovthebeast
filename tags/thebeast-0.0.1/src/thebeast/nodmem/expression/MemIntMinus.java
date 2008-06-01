package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntMinus;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.type.IntType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:40:54
 */
public class MemIntMinus extends AbstractMemExpression<IntType> implements IntMinus {

  private IntExpression leftHandSide, rightHandSide;

  public MemIntMinus(IntType type, IntExpression leftHandSide, IntExpression rightHandSide) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntMinus(this);
  }

  public IntExpression leftHandSide() {
    return leftHandSide;
  }

  public IntExpression rightHandSide() {
    return rightHandSide;
  }
}
