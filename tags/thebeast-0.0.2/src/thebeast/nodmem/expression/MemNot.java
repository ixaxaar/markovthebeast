package thebeast.nodmem.expression;

import thebeast.nod.expression.BoolExpression;
import thebeast.nod.expression.Not;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.type.BoolType;
import thebeast.nodmem.type.MemBoolType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Feb-2007 Time: 20:10:39
 */
public class MemNot extends AbstractMemExpression<BoolType> implements Not {

  private BoolExpression expression;

  protected MemNot(BoolExpression expression) {
    super(MemBoolType.BOOL);
    this.expression = expression;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitNot(this);
  }

  public BoolExpression expression() {
    return expression;
  }
}
