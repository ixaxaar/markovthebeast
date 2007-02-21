package thebeast.nod.statement;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.VariableReference;
import thebeast.nod.expression.Expression;
import thebeast.nod.expression.BoolExpression;
import thebeast.nod.type.RelationType;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Variable;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface StatementFactory {
  Insert createInsert(RelationVariable relationTarget, RelationExpression relationExp);

  Assign createAssign(Variable relationTarget, Expression value);

  AttributeAssign createAttributeAssign(String name, Expression value);

  RelationUpdate createRelationUpdate(RelationVariable variable, BoolExpression where, List<AttributeAssign> assigns);

  RelationUpdate createRelationUpdate(RelationVariable variable, AttributeAssign assign);


}
