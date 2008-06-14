package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.expression.IntOperatorInvocation;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.Operator;
import thebeast.nod.expression.Expression;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemIntOperatorInvocation extends MemOperatorInvocation<IntType> implements IntOperatorInvocation {
  protected MemIntOperatorInvocation(IntType type, Operator<IntType> operator, List<Expression> args) {
    super(type, operator, args);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntOperatorInvocation(this);
  }
}
