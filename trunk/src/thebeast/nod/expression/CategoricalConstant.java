package thebeast.nod.expression;

import thebeast.nod.type.CategoricalType;
import thebeast.nod.value.CategoricalValue;

/**
 * @author Sebastian Riedel
 */
public interface CategoricalConstant extends Constant<CategoricalType, CategoricalValue>,CategoricalExpression {

  String representation();

}
