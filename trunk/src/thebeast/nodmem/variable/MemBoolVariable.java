package thebeast.nodmem.variable;

import thebeast.nod.value.IntValue;
import thebeast.nod.value.BoolValue;
import thebeast.nod.type.IntType;
import thebeast.nod.type.BoolType;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.BoolVariable;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.NoDServer;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.value.MemInt;
import thebeast.nodmem.value.MemBool;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Jan-2007 Time: 17:36:07
 */
public class MemBoolVariable extends AbstractMemVariable<BoolValue, BoolType> implements BoolVariable {


  public MemBoolVariable(NoDServer server, BoolType type, MemChunk chunk) {
    super(server, type, chunk);
  }

  public void destroy() {

  }

  public void copy(AbstractMemVariable other) {
    chunk.intData[pointer.xInt] = other.chunk.intData[other.pointer.xInt];
  }

  public BoolValue value(){
    return new MemBool(chunk, pointer.xInt, type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitBoolVariable(this);
  }
}
