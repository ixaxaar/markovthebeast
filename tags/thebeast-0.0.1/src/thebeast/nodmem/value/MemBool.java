package thebeast.nodmem.value;

import thebeast.nod.type.BoolType;
import thebeast.nod.value.BoolValue;
import thebeast.nod.value.ValueVisitor;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;

/**
 * @author Sebastian Riedel
 */
public class MemBool extends AbstractMemValue<BoolType> implements BoolValue {

  private MemChunk chunk;
  private int pointer;

  public MemBool(MemChunk chunk, int pointer, BoolType type) {
    super(type);
    this.chunk = chunk;
    this.pointer = pointer;
  }

  public void acceptValueVisitor(ValueVisitor visitor) {
    visitor.visitBool(this);
  }

  public boolean getBool() {
    return chunk.intData[pointer] == 1;
  }


  public int hashCode() {
    return chunk.intData[pointer];
  }

  public boolean equals(Object object) {
    if (object instanceof MemBool) {
      MemBool memBool = (MemBool) object;
      return memBool.chunk.intData[memBool.pointer] == chunk.intData[pointer];
    }
    return false;
  }

  void setPointer(MemVector pointer) {
    this.pointer = pointer.xInt;
  }

  public void copyFrom(AbstractMemValue v) {
    MemBool other = (MemBool) v;
    chunk.intData[pointer] = other.chunk.intData[other.pointer];
  }

  public String toString(){
    return String.valueOf(getBool());
  }
}
