package thebeast.nod.variable;

import thebeast.nod.value.RelationValue;
import thebeast.nod.value.ArrayValue;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.ArrayType;
import thebeast.nod.expression.ArrayExpression;

/**
 * @author Sebastian Riedel
 */
public interface ArrayVariable extends Variable<ArrayValue,ArrayType>, ArrayExpression {
  int byteSize();

  double doubleValue(int index);

}
