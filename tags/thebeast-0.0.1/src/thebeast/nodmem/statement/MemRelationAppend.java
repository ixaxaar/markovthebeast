package thebeast.nodmem.statement;

import thebeast.nod.statement.Insert;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.statement.RelationAppend;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.expression.RelationExpression;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.type.MemHeading;

/**
 * @author Sebastian Riedel
 */
public class MemRelationAppend implements RelationAppend {

  private RelationVariable relationTarget;
  private RelationExpression relationExp;
  private MemChunk buffer;

  public MemRelationAppend(RelationVariable relationTarget, RelationExpression relationExp) {
    this.relationTarget = relationTarget;
    this.relationExp = relationExp;
    MemHeading heading = (MemHeading) relationTarget.type().heading();
    buffer = new MemChunk(1, new int[0], new double[0], new MemChunk[]{
            new MemChunk(0, 0, heading.getDim())});
  }

  public MemChunk getBuffer() {
    return buffer;
  }

  public RelationVariable relationTarget() {
    return relationTarget;
  }

  public RelationExpression relationExp() {
    return relationExp;
  }

  public void acceptStatementVisitor(StatementVisitor visitor) {
    visitor.visitRelationAppend(this);
  }
}
