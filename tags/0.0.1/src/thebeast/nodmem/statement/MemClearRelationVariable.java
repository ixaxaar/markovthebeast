package thebeast.nodmem.statement;

import thebeast.nod.statement.ClearRelationVariable;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.variable.RelationVariable;
import thebeast.nodmem.variable.MemRelationVariable;

/**
 * @author Sebastian Riedel
 */
public class MemClearRelationVariable implements ClearRelationVariable {

  private MemRelationVariable target;

  public MemClearRelationVariable(MemRelationVariable target) {
    this.target = target;
  }


  public RelationVariable variable() {
    return target;
  }

  public void acceptStatementVisitor(StatementVisitor visitor) {
    visitor.visitClearRelationVariable(this);
  }
}
