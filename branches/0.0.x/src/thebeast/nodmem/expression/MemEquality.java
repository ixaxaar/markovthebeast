package thebeast.nodmem.expression;

import thebeast.nod.expression.Equality;
import thebeast.nod.expression.Expression;
import thebeast.nod.expression.ExpressionVisitor;


/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:46:40
 */
public class MemEquality extends BinaryComparison<Expression,Expression> implements Equality {
  public MemEquality(Expression expr1, Expression expr2) {
    super(expr1, expr2);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitEquality(this);
  }
}
