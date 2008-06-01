package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.RelationType;
import thebeast.nod.expression.*;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemRelationOperatorInvocation extends MemOperatorInvocation<RelationType> implements RelationOperatorInvocation {
  protected MemRelationOperatorInvocation(RelationType type, Operator<RelationType> operator, List<Expression> args) {
    super(type, operator, args);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitRelationOperatorInvocation(this);
  }
}
