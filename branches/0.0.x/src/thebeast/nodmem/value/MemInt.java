package thebeast.nodmem.value;

import thebeast.nod.type.IntType;
import thebeast.nod.value.ValueVisitor;
import thebeast.nod.value.IntValue;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;

/**
 * @author Sebastian Riedel
 */
public class MemInt extends AbstractMemValue<IntType> implements IntValue {

  private MemChunk chunk;
  private int pointer;

  public MemInt(MemChunk chunk, int pointer, IntType type) {
    super(type);
    this.chunk = chunk;
    this.pointer = pointer;
  }

  public void acceptValueVisitor(ValueVisitor visitor) {
    visitor.visitInt(this);
  }

  public int getInt() {
    return chunk.intData[pointer];
  }


  public int hashCode() {
    return chunk.intData[pointer];
  }

  public boolean equals(Object object) {
    if (object instanceof MemInt) {
      MemInt memInt = (MemInt) object;
      return memInt.chunk.intData[memInt.pointer] == chunk.intData[pointer];
    }
    return false;
  }

  void setPointer(MemVector pointer) {
    this.pointer = pointer.xInt;
  }

  public void copyFrom(AbstractMemValue v) {
    MemInt other = (MemInt) v;
    chunk.intData[pointer] = other.chunk.intData[other.pointer];
  }

  public String toString(){
    return String.valueOf(getInt());
  }
}

