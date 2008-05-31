package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.RelationType;
import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:40:54
 */
public class MemRelationMinus extends AbstractMemExpression<RelationType> implements RelationMinus {

  private RelationExpression leftHandSide, rightHandSide;

  public MemRelationMinus(RelationType type, RelationExpression leftHandSide, RelationExpression rightHandSide) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitRelationMinus(this);
  }

  public RelationExpression leftHandSide() {
    return leftHandSide;
  }

  public RelationExpression rightHandSide() {
    return rightHandSide;
  }
}
