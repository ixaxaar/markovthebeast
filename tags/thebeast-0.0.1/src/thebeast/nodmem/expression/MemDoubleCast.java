package thebeast.nodmem.expression;

import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemDoubleCast extends AbstractMemExpression<DoubleType> implements DoubleCast {

  private IntExpression intExpression;

  public MemDoubleCast(DoubleType type, IntExpression intExpression) {
    super(type);
    this.intExpression = intExpression;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleCast(this);
  }


  public IntExpression intExpression() {
    return intExpression;
  }
}
