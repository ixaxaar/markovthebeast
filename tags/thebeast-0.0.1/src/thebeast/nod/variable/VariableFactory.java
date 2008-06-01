package thebeast.nod.variable;

import thebeast.nod.type.IntType;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.ArrayValue;

/**
 * @author Sebastian Riedel
 */
public interface VariableFactory {

    IntVariable createIntVariable(int value);
    RelationVariable createRelationVariable(RelationValue value);
    TupleVariable createTupleVariable(TupleValue value);
    ArrayVariable createArrayVariable(ArrayValue value);

}
