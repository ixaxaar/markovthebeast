package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.expression.IntMin;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntMax;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemIntMax extends AbstractMemExpression<IntType> implements IntMax {

  private IntExpression leftHandSide, rightHandSide;

  public MemIntMax(IntType type, IntExpression leftHandSide, IntExpression rightHandSide) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntMax(this);
  }

  public IntExpression leftHandSide() {
    return leftHandSide;
  }

  public IntExpression rightHandSide() {
    return rightHandSide;
  }

}
