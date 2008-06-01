package thebeast.nod.type;

import thebeast.nod.value.DoubleValue;

/**
 * @author Sebastian Riedel
 */
public interface DoubleType extends ScalarType {

    double from();
    double to();
    DoubleValue value(double value);

}
