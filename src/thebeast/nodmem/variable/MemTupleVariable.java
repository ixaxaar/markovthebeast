package thebeast.nodmem.variable;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.type.TupleType;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.TupleVariable;
import thebeast.nod.NoDServer;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemTupleType;
import thebeast.nodmem.value.MemTuple;

/**
 * @author Sebastian Riedel
 */
public class MemTupleVariable extends AbstractMemVariable<TupleValue, TupleType> implements TupleVariable {

  protected MemTupleVariable(NoDServer server, TupleValue value, TupleType type) {
    super(server, value, type);
  }

  public MemTupleVariable(NoDServer server, TupleType type){
    super(server, type, new MemChunk(1,new int[0],new double[0],
            new MemChunk[]{new MemChunk(((MemHeading)type.heading()).getDim())}));
  }

  public void destroy() {

  }

  public MemTuple value(){
    return new MemTuple(chunk.chunkData[0],new MemVector(), (MemTupleType) type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitTupleVariable(this);
  }

  public void copy(AbstractMemVariable other) {
    chunk.chunkData[pointer.xChunk] = other.chunk.chunkData[other.pointer.xChunk].copy();
  }
}
