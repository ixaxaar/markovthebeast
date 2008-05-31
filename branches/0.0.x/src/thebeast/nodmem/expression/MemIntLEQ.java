package thebeast.nodmem.expression;

import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemIntLEQ extends BinaryComparison<IntExpression,IntExpression> implements IntLEQ {
  public MemIntLEQ(IntExpression expr1, IntExpression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntLEQ(this);
  }
}
