package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.expression.IntGEQ;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemIntGEQ extends BinaryComparison<IntExpression,IntExpression> implements IntGEQ {
  public MemIntGEQ(IntExpression expr1, IntExpression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntGEQ(this);
  }
}
