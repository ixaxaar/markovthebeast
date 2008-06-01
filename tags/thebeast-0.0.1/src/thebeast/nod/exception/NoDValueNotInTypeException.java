package thebeast.nod.exception;

import thebeast.nodmem.type.MemIntType;
import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public class NoDValueNotInTypeException extends RuntimeException {

    private Type type;
    private Object value;

    public NoDValueNotInTypeException(Type type, Object value) {
        super(value + " is not a member of " + type);
        this.type = type;
        this.value = value;
    }


    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
