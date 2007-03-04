package thebeast.nodmem.variable;

import thebeast.nod.value.IntValue;
import thebeast.nod.type.IntType;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.NoDServer;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.value.MemInt;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Jan-2007 Time: 17:36:07
 */
public class MemIntVariable extends AbstractMemVariable<IntValue, IntType> implements IntVariable {
  protected MemIntVariable(NoDServer server, IntValue value) {
    super(server, value, value.type());
    chunk = new MemChunk(1,1,1,0,0);
    pointer = new MemVector(0,0,0);
  }

  public MemIntVariable(NoDServer server, IntType type, MemChunk chunk) {
    super(server, type, chunk);
  }

  public void destroy() {
      
  }

  public boolean copy(AbstractMemVariable other) {
    chunk.intData[pointer.xInt] = other.chunk.intData[other.pointer.xInt];
    return true;
  }

  public IntValue value(){
    return new MemInt(chunk, pointer.xInt, type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntVariable(this);
  }
}
