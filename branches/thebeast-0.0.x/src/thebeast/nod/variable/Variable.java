package thebeast.nod.variable;

import thebeast.nod.value.Value;
import thebeast.nod.type.Type;
import thebeast.nod.expression.Expression;

/**
 * @author Sebastian Riedel
 */
public interface Variable<V extends Value, T extends Type> extends Expression<T> {

  T type();

  V value();

  String label();

  void setLabel(String label);
}
