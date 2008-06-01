package thebeast.nod.value;

import thebeast.nod.type.SetType;

import java.util.Iterator;

/**
 * @author Sebastian Riedel
 */
public interface SetValue<T extends SetType> extends Value<T> {

    Iterator<? extends Value> values();
    int size();

}
