package thebeast.nodmem.expression;

import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.expression.DoubleLEQ;
import thebeast.nod.expression.ExpressionVisitor;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemDoubleLEQ extends BinaryComparison<DoubleExpression,DoubleExpression>
        implements DoubleLEQ {
  public MemDoubleLEQ(DoubleExpression expr1, DoubleExpression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleLEQ(this);
  }
}
