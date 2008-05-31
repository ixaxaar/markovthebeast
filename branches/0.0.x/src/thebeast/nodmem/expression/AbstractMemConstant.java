package thebeast.nodmem.expression;

import thebeast.nod.expression.Constant;
import thebeast.nod.type.Type;
import thebeast.nod.value.Value;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractMemConstant<T extends Type, V extends Value> extends AbstractMemExpression<T>
        implements Constant<T, V> {
  private V value;

  protected AbstractMemConstant(T type, V value) {
    super(type);
    this.value = value;
  }


  protected AbstractMemConstant(T type) {
    super(type);
  }

  public V value() {
    return value;
  }

}
