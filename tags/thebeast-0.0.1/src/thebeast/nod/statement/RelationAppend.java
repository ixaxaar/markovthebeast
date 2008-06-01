package thebeast.nod.statement;

import thebeast.nod.variable.RelationVariable;
import thebeast.nod.expression.RelationExpression;

/**
 * @author Sebastian Riedel
 */
public interface RelationAppend extends Statement {
    RelationVariable relationTarget();
    RelationExpression relationExp();

}
