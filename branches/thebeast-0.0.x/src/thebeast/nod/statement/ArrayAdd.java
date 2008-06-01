package thebeast.nod.statement;

import thebeast.nod.variable.ArrayVariable;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.expression.ArrayExpression;

/**
 * @author Sebastian Riedel
 */
public interface ArrayAdd extends Statement {
  ArrayVariable variable();
  ArrayExpression argument();
  DoubleExpression scale();
}
