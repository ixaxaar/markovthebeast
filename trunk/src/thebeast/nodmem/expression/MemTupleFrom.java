package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.TupleFrom;
import thebeast.nod.type.TupleType;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemTupleType;

/**
 * @author Sebastian Riedel
 */
public class MemTupleFrom extends AbstractMemExpression<TupleType> implements TupleFrom {

  private RelationExpression relation;

  public MemTupleFrom(RelationExpression relation){
    super(new MemTupleType((MemHeading) relation.type().heading()));
    this.relation = relation;
  }

  public RelationExpression relation() {
    return relation;
  }



  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitTupleFrom(this);
  }

}
