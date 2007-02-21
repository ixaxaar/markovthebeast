package thebeast.nodmem.expression;

import thebeast.nod.expression.Join;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.Attribute;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class MemJoin extends AbstractMemExpression<RelationType> implements Join  {

  private ArrayList<RelationExpression> relations;

  protected MemJoin(RelationType type, List<RelationExpression> relations) {
    super(type);
    this.relations = new ArrayList<RelationExpression>(relations);
  }

  public List<RelationExpression> relations() {
    return relations;
  }

  public List<Attribute> joinAttributes() {
    return null;
  }

  public List<Attribute> joinAttributesFor(int index) {
    return null;
  }

  public List<Integer> joinRelationsFor(Attribute attribute) {
    return null;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitJoin(this);
  }
}
