package thebeast.nodmem.expression;

import thebeast.nod.type.RelationType;
import thebeast.nod.type.TupleType;
import thebeast.nod.expression.*;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemTupleOperatorInvocation extends MemOperatorInvocation<TupleType> implements TupleOperatorInvocation {
  protected MemTupleOperatorInvocation(TupleType type, Operator<TupleType> operator, List<Expression> args) {
    super(type, operator, args);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitTupleOperatorInvocation(this);
  }
}
