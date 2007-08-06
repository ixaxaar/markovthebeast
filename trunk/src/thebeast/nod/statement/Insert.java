package thebeast.nod.statement;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.VariableReference;
import thebeast.nod.type.RelationType;
import thebeast.nod.variable.RelationVariable;

/**
 * @author Sebastian Riedel
 */
public interface Insert extends Statement {
  RelationVariable relationTarget();

  RelationExpression relationExp();

}
