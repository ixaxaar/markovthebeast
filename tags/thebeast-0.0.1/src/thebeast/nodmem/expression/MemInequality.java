package thebeast.nodmem.expression;

import thebeast.nod.expression.Expression;
import thebeast.nod.expression.Equality;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.Inequality;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemInequality extends BinaryComparison<Expression,Expression> implements Inequality {
  public MemInequality(Expression expr1, Expression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitInequality(this);
  }
}
