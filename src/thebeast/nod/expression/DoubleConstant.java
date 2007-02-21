package thebeast.nod.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.DoubleType;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.DoubleValue;

/**
 * @author Sebastian Riedel
 */
public interface DoubleConstant extends Constant<DoubleType, DoubleValue>, DoubleExpression {

  double getDouble();

}
