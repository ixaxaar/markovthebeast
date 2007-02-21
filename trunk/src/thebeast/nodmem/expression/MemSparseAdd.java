package thebeast.nodmem.expression;

import thebeast.nod.type.RelationType;
import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:40:54
 */
public class MemSparseAdd extends AbstractMemExpression<RelationType> implements SparseAdd {

  private RelationExpression leftHandSide, rightHandSide;
  private String indexAttribute, valueAttribute;
  private DoubleExpression scale;

  public MemSparseAdd(RelationType type, RelationExpression leftHandSide, RelationExpression rightHandSide,
                      DoubleExpression scale, String indexAttribute, String valueAttribute) {
    super(type);
    this.leftHandSide = leftHandSide;
    this.rightHandSide = rightHandSide;
    this.scale = scale;
    this.indexAttribute = indexAttribute;
    this.valueAttribute = valueAttribute;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitSparseAdd(this);
  }

  public RelationExpression leftHandSide() {
    return leftHandSide;
  }

  public RelationExpression rightHandSide() {
    return rightHandSide;
  }

  public DoubleExpression scale() {
    return scale;
  }

  public String indexAttribute() {
    return indexAttribute;
  }

  public String valueAttribute() {
    return valueAttribute;
  }
}
