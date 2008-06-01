package thebeast.nod.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.value.IntValue;

/**
 * @author Sebastian Riedel
 */
public interface IntConstant extends Constant<IntType,IntValue>, IntExpression {

  int getInt();
  
}
