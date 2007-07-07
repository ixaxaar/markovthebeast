package thebeast.nodmem.statement;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Insert;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.variable.RelationVariable;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.type.MemHeading;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemInsert implements Insert {

  private RelationVariable relationTarget;
  private RelationExpression relationExp;
  private MemChunk buffer;

  private static LinkedList<WeakReference<Insert>> references = new LinkedList<WeakReference<Insert>>();
  private static ReferenceQueue<Insert> queue = new ReferenceQueue<Insert>();


  public MemInsert(RelationVariable relationTarget, RelationExpression relationExp) {
    this.relationTarget = relationTarget;
    this.relationExp = relationExp;
    MemHeading heading = (MemHeading) relationTarget.type().heading();
    buffer = new MemChunk(1, new int[0], new double[0], new MemChunk[]{
            new MemChunk(0, 0, heading.getDim())});
    references.add(new WeakReference<Insert>(this, queue));
  }

  public static List<WeakReference<Insert>> references() {
    return references;
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
    visitor.visitInsert(this);
  }

  public int byteSize(){
    return buffer.byteSize();
  }

}
