package thebeast.nod.expression;

import thebeast.nod.type.Heading;
import thebeast.nod.type.TupleType;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface RelationSelector extends RelationExpression {
  Heading heading();

  List<TupleExpression> tupleExpressions();

  boolean unify();
}
