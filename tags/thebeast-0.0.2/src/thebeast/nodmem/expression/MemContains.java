package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.Contains;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.TupleExpression;
import thebeast.nod.type.BoolType;

/**
 * @author Sebastian Riedel
 */
public class MemContains extends BinaryComparison<RelationExpression,TupleExpression> implements Contains {



  public MemContains(RelationExpression relation, TupleExpression tuple) {
    super(relation,tuple);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitContains(this);
  }

  public RelationExpression relation() {
    return expr1;
  }

  public TupleExpression tuple() {
    return expr2;
  }
}
