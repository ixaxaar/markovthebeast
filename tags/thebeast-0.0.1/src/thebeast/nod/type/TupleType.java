package thebeast.nod.type;

import thebeast.nod.type.Type;
import thebeast.nod.type.Heading;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.Value;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface TupleType extends Type {

    Heading heading();
    TupleValue tuple(List<Value> values);
    TupleValue tuple(Value ... values);

}
