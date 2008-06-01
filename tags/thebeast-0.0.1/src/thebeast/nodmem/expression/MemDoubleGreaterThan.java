package thebeast.nodmem.expression;

import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemDoubleGreaterThan extends BinaryComparison<DoubleExpression,DoubleExpression>
        implements DoubleGreaterThan {
  public MemDoubleGreaterThan(DoubleExpression expr1, DoubleExpression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleGreaterThan(this);
  }
}
