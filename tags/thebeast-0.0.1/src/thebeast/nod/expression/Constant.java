package thebeast.nod.expression;

import thebeast.nod.type.Type;
import thebeast.nod.type.ScalarType;
import thebeast.nod.value.Value;
import thebeast.nod.value.ScalarValue;

/**
 * @author Sebastian Riedel
 */
public interface Constant<T extends Type, V extends Value> extends Expression<T>{
    V value();

}
