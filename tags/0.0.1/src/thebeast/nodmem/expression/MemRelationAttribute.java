package thebeast.nodmem.expression;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.RelationType;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.RelationAttribute;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 20:15:59
 */
public class MemRelationAttribute extends MemAttributeExpression<RelationType> implements RelationAttribute {
  public MemRelationAttribute(RelationType type, String prefix, Attribute attribute) {
    super(type, prefix, attribute);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitRelationAttribute(this);
  }
}
