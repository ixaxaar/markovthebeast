package thebeast.nod.statement;

import thebeast.nod.variable.ArrayVariable;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.DoubleExpression;

/**
 * @author Sebastian Riedel
 */
public interface ArraySparseAdd extends Statement {
  ArrayVariable variable();
  RelationExpression sparseVector();
  String indexAttribute();
  String valueAttribute();

  DoubleExpression scale();
}
