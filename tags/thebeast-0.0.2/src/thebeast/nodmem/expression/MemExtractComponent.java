package thebeast.nodmem.expression;

import thebeast.nod.type.Type;
import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 17:28:15
 */
public abstract class MemExtractComponent<T extends Type> extends AbstractMemExpression<T>
        implements ExtractComponent<T> {

  private TupleExpression tuple;
  private String name;

  public MemExtractComponent(T type, TupleExpression tuple, String name) {
    super(type);
    this.tuple = tuple;
    this.name = name;
  }


  public TupleExpression tuple() {
    return tuple;
  }

  public String name() {
    return name;
  }
}
