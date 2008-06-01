package thebeast.nod.type;

import thebeast.nod.type.ScalarType;
import thebeast.nod.value.IntValue;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface IntType extends ScalarType {

    int from();
    int to();
    IntValue value(int value);

    Iterable<IntValue> values();

    int size();



}
