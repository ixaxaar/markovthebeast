package thebeast.nod.expression;

import thebeast.nod.type.Type;
import thebeast.nod.type.Attribute;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 20:01:39
 */
public interface AttributeExpression<T extends Type> extends Expression<T>  {
  String prefix();
  Attribute attribute();
}
