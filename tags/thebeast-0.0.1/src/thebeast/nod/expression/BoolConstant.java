package thebeast.nod.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.BoolType;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.BoolValue;

/**
 * @author Sebastian Riedel
 */
public interface BoolConstant extends Constant<BoolType, BoolValue>, BoolExpression {

  boolean getBoolean();

}
