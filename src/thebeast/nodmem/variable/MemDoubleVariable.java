package thebeast.nodmem.variable;

import thebeast.nod.value.BoolValue;
import thebeast.nod.value.DoubleValue;
import thebeast.nod.type.BoolType;
import thebeast.nod.type.DoubleType;
import thebeast.nod.variable.BoolVariable;
import thebeast.nod.variable.DoubleVariable;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.NoDServer;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.value.MemBool;
import thebeast.nodmem.value.MemDouble;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Jan-2007 Time: 17:36:07
 */
public class MemDoubleVariable extends AbstractMemVariable<DoubleValue, DoubleType> implements DoubleVariable {


  public MemDoubleVariable(NoDServer server, DoubleType type, MemChunk chunk) {
    super(server, type, chunk);
  }

  public void destroy() {

  }

  public void copy(AbstractMemVariable other) {
    chunk.doubleData[pointer.xDouble] = other.chunk.doubleData[other.pointer.xDouble];
  }

  public DoubleValue value(){
    return new MemDouble(chunk, pointer.xDouble, type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleVariable(this);
  }
}
