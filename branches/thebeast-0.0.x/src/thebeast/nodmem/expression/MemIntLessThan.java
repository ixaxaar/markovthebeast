package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.expression.IntLessThan;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemIntLessThan extends BinaryComparison<IntExpression,IntExpression> implements IntLessThan {
  public MemIntLessThan(IntExpression expr1, IntExpression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntLessThan(this);
  }
}
