package thebeast.nod.variable;

import thebeast.nod.value.IntValue;
import thebeast.nod.value.BoolValue;
import thebeast.nod.type.IntType;
import thebeast.nod.type.BoolType;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.expression.BoolExpression;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Jan-2007 Time: 16:51:14
 */
public interface BoolVariable extends Variable<BoolValue, BoolType>, BoolExpression {
}
