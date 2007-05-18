package thebeast.nodmem.type;

import thebeast.nod.type.ArrayType;
import thebeast.nod.type.TypeVisitor;
import thebeast.nod.type.Type;
import thebeast.nod.value.Value;
import thebeast.nodmem.value.AbstractMemValue;
import thebeast.nodmem.value.MemTuple;
import thebeast.nodmem.value.MemArray;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 17:14:41
 */
public class MemArrayType extends AbstractMemType implements ArrayType {

  private Type instanceType;

  public MemArrayType(Type instanceType) {
    //super(DataType.CHUNK, 0,0,1);
    setNumChunkCols(1);
    //super(DataType.CHUNK, ((AbstractMemType)instanceType).getDim());
    this.instanceType = instanceType;
  }

  public Value emptyValue() {
    return null;
  }

  public AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemArray(chunk.chunkData[pointer.xChunk], new MemVector(), this);
  }

  public void acceptTypeVisitor(TypeVisitor visitor) {
    visitor.visitArrayType(this);
  }

  public Type instanceType() {
    return instanceType;
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemArrayType that = (MemArrayType) o;

    return instanceType.equals(that.instanceType);

  }

  public int hashCode() {
    return instanceType.hashCode();
  }
}
