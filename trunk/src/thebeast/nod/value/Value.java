package thebeast.nod.value;

import thebeast.nod.type.Type;
import thebeast.nod.expression.Constant;

/**
 * @author Sebastian Riedel
 */
public interface Value<T extends Type> {

    public void acceptValueVisitor(ValueVisitor visitor);
    public T type();

}
