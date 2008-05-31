package thebeast.nodmem.variable;

import thebeast.nod.variable.*;
import thebeast.nod.value.ArrayValue;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nodmem.type.MemIntType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Jan-2007 Time: 17:31:52
 */
public class MemVariableFactory implements VariableFactory {
  public IntVariable createIntVariable(int value) {
    return new MemIntVariable(null, MemIntType.INT.value(value));
  }

  public RelationVariable createRelationVariable(RelationValue value) {
    return null;
  }

  public TupleVariable createTupleVariable(TupleValue value) {
    return null;
  }

  public ArrayVariable createArrayVariable(ArrayValue value) {
    return null;
  }
}
