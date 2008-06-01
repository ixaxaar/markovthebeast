package thebeast.nod.value;

import thebeast.nod.type.BoolType;

/**
 * @author Sebastian Riedel
 */
public interface BoolValue extends ScalarValue<BoolType> {
    boolean getBool();
}
