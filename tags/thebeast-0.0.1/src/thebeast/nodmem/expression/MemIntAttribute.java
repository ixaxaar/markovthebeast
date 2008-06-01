package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.Attribute;
import thebeast.nod.expression.IntAttribute;
import thebeast.nod.expression.ExpressionVisitor;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 20:15:59
 */
public class MemIntAttribute extends MemAttributeExpression<IntType> implements IntAttribute {
  public MemIntAttribute(IntType type, String prefix, Attribute attribute) {
    super(type, prefix, attribute);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntAttribute(this);
  }
}
