package thebeast.nodmem.expression;

import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.expression.DoubleGreaterThan;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.DoubleLessThan;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemDoubleLessThan extends BinaryComparison<DoubleExpression,DoubleExpression>
        implements DoubleLessThan {
  public MemDoubleLessThan(DoubleExpression expr1, DoubleExpression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleLessThan(this);
  }
}
