package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.expression.Count;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.RelationExpression;
import thebeast.nodmem.type.MemIntType;

/**
 * @author Sebastian Riedel
 */
public class MemCount extends AbstractMemExpression<IntType> implements Count {

  private RelationExpression relation;


  public MemCount(RelationExpression relation) {
    super(MemIntType.INT);
    this.relation = relation;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitCount(this);
  }

  public RelationExpression relation() {
    return relation;
  }
}
