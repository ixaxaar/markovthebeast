package thebeast.nodmem.statement;

import thebeast.nod.expression.Expression;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.BoolExpression;
import thebeast.nod.statement.*;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Variable;
import thebeast.nodmem.variable.AbstractMemVariable;

import java.util.List;
import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class MemStatementFactory implements StatementFactory {

  public Insert createInsert(RelationVariable relationTarget, RelationExpression relationExp) {
    return new MemInsert(relationTarget, relationExp);
  }

  public Assign createAssign(Variable variable, Expression value) {
    return new MemAssign((AbstractMemVariable) variable,value);
  }

  public AttributeAssign createAttributeAssign(String name, Expression value) {
    return new MemAttributeAssign(name,value);
  }

  public RelationUpdate createRelationUpdate(RelationVariable variable, BoolExpression where, List<AttributeAssign> assigns){
    return new MemRelationUpdate(variable, where, assigns);
  }

  public RelationUpdate createRelationUpdate(RelationVariable variable, AttributeAssign assign) {
    LinkedList<AttributeAssign> assigns = new LinkedList<AttributeAssign>();
    assigns.add(assign);
    return new MemRelationUpdate(variable, null, assigns);
  }

}
