package thebeast.nodmem.expression;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.CategoricalAttribute;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 20:15:59
 */
public class MemCategoricalAttribute extends MemAttributeExpression<CategoricalType> implements CategoricalAttribute {
  public MemCategoricalAttribute(CategoricalType type, String prefix, Attribute attribute) {
    super(type, prefix, attribute);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitCategoricalAttribute(this);
  }
}
