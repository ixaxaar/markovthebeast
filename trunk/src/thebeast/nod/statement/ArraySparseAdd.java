package thebeast.nod.statement;

import thebeast.nod.variable.ArrayVariable;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.DoubleExpression;

/**
 * @author Sebastian Riedel
 */
public interface ArraySparseAdd extends Statement {

  enum Sign {FREE, NONNEGATIVE, NONPOSITIVE
  }

  ArrayVariable variable();
  RelationExpression sparseVector();
  String indexAttribute();
  String valueAttribute();
  DoubleExpression scale();
  Sign sign();
}
