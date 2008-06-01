package thebeast.nodmem.expression;

import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.DoubleTimes;
import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.DoubleDivide;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemDoubleDivide extends AbstractMemExpression<DoubleType> implements DoubleDivide {

  private DoubleExpression leftHandSide, rightHandSide;

  public MemDoubleDivide(DoubleType type, DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleDivide(this);
  }

  public DoubleExpression leftHandSide() {
    return leftHandSide;
  }

  public DoubleExpression rightHandSide() {
    return rightHandSide;
  }

}
