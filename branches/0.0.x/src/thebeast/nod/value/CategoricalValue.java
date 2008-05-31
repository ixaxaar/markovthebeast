package thebeast.nod.value;

import thebeast.nod.type.CategoricalType;

/**
 * @author Sebastian Riedel
 */
public interface CategoricalValue extends ScalarValue<CategoricalType> {

    String representation();
    int index();

}
