package thebeast.nodmem.expression;

import thebeast.nod.expression.ArrayAccess;
import thebeast.nod.expression.ArrayExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.type.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 17:28:15
 */
public abstract class MemArrayAccess<T extends Type> extends AbstractMemExpression<T> implements ArrayAccess<T> {

  private IntExpression index;
  private ArrayExpression array;

  public MemArrayAccess(T type, ArrayExpression array, IntExpression index) {
    super(type);
    this.array = array;
    this.index = index;
  }

  public ArrayExpression array() {
    return array;
  }

  public IntExpression index() {
    return index;
  }
}
