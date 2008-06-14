package thebeast.nodmem.variable;

import thebeast.nod.value.CategoricalValue;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.variable.CategoricalVariable;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.NoDServer;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.value.MemCategorical;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Jan-2007 Time: 17:36:07
 */
public class MemCategoricalVariable extends AbstractMemVariable<CategoricalValue, CategoricalType>
        implements CategoricalVariable {


  public MemCategoricalVariable(NoDServer server, CategoricalType type, MemChunk chunk) {
    super(server, type, chunk);
  }

  public void destroy() {

  }

  public boolean copy(AbstractMemVariable other) {
    chunk.intData[pointer.xInt] = other.chunk.intData[other.pointer.xInt];
    return true;
  }

  public CategoricalValue value(){
    return new MemCategorical(chunk, pointer.xInt, type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitCategoricalVariable(this);
  }
}
