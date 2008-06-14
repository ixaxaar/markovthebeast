package thebeast.nodmem.statement;

import thebeast.nod.statement.AttributeAssign;
import thebeast.nod.expression.Expression;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 19:16:15
 */
public class MemAttributeAssign implements AttributeAssign {

  private String name;
  private Expression value;

  public MemAttributeAssign(String name, Expression value) {
    this.value = value;
    this.name = name;
  }

  public String attributeName() {
    return name;
  }

  public Expression expression() {
    return value;
  }
}
