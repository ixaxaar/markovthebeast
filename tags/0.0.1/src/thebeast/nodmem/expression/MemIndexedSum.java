package thebeast.nodmem.expression;

import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.IndexedSum;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.ArrayExpression;
import thebeast.nod.expression.RelationExpression;

/**
 * @author Sebastian Riedel
 */
public class MemIndexedSum extends AbstractMemExpression<DoubleType> implements IndexedSum {

  private ArrayExpression array;
  private RelationExpression indexRelation;
  private String indexAttribute, scaleAttribute;


  public MemIndexedSum(ArrayExpression array,
                       RelationExpression indexRelation, String indexAttribute, String scaleAttribute) {
    super((DoubleType) array.type().instanceType());
    this.array = array;
    this.indexRelation = indexRelation;
    this.indexAttribute = indexAttribute;
    this.scaleAttribute = scaleAttribute;
  }

  protected MemIndexedSum(DoubleType type) {
    super(type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIndexedSum(this);
  }

  public ArrayExpression array() {
    return array;
  }

  public RelationExpression indexRelation() {
    return indexRelation;
  }

  public String indexAttribute() {
    return indexAttribute;
  }

  public String scaleAttribute() {
    return scaleAttribute;
  }
}
