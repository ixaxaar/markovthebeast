package thebeast.nodmem.expression;

import thebeast.nod.type.Type;
import thebeast.nod.type.Attribute;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 20:11:16
 */
public abstract class MemAttributeExpression<T extends Type> extends AbstractMemExpression<T> {

  protected Attribute attribute;
  protected String prefix;

  protected MemAttributeExpression(T type, String prefix, Attribute attribute) {
    super(type);
    this.prefix = prefix;
    this.attribute = attribute;
  }


  public Attribute attribute() {
    return attribute;
  }

  public String prefix() {
    return prefix;
  }
}
