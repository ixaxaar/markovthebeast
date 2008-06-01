package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemDoubleAdd extends AbstractMemExpression<DoubleType> implements DoubleAdd {

  private DoubleExpression leftHandSide, rightHandSide;

  public MemDoubleAdd(DoubleType type, DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleAdd(this);
  }

  public DoubleExpression leftHandSide() {
    return leftHandSide;
  }

  public DoubleExpression rightHandSide() {
    return rightHandSide;
  }

}
