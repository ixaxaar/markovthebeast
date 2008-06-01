package thebeast.nodmem.expression;

import thebeast.nod.expression.BoolExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.Restrict;
import thebeast.nod.type.RelationType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Jan-2007 Time: 14:40:01
 */
public class MemRestrict extends AbstractMemExpression<RelationType> implements Restrict {

  private RelationExpression relation;
  private BoolExpression where;

  protected MemRestrict(RelationExpression relation, BoolExpression where) {
    super(relation.type());
    this.relation = relation;
    this.where = where;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitRestrict(this);
  }

  public RelationExpression relation() {
    return relation;
  }

  public BoolExpression where() {
    return where;
  }
}
