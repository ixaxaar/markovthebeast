package thebeast.nod.variable;

import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.TupleType;
import thebeast.nod.expression.TupleExpression;

/**
 * @author Sebastian Riedel
 */
public interface TupleVariable extends Variable<TupleValue,TupleType>, TupleExpression {
}
