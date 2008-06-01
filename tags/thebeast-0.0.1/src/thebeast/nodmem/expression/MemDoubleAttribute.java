package thebeast.nodmem.expression;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.DoubleAttribute;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 20:15:59
 */
public class MemDoubleAttribute extends MemAttributeExpression<DoubleType> implements DoubleAttribute {
  public MemDoubleAttribute(DoubleType type, String prefix, Attribute attribute) {
    super(type, prefix, attribute);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleAttribute(this);
  }
}
