package thebeast.nodmem.value;

import thebeast.nod.type.IntType;
import thebeast.nod.type.DoubleType;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.ValueVisitor;
import thebeast.nod.value.DoubleValue;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;

/**
 * @author Sebastian Riedel
 */
public class MemDouble extends AbstractMemValue<DoubleType> implements DoubleValue {

  private MemChunk chunk;
  private int pointer;

  public MemDouble(MemChunk chunk, int pointer, DoubleType type) {
    super(type);
    this.chunk = chunk;
    this.pointer = pointer;
  }

  public void acceptValueVisitor(ValueVisitor visitor) {
    visitor.visitDouble(this);
  }


  public int hashCode() {
    return (int) chunk.doubleData[pointer];
  }

  public boolean equals(Object object) {
    if (object instanceof MemDouble) {
      MemDouble memDouble = (MemDouble) object;
      return memDouble.chunk.doubleData[memDouble.pointer] == chunk.doubleData[pointer];
    }
    return false;
  }

  void setPointer(MemVector pointer) {
    this.pointer = pointer.xInt;
  }

  public void copyFrom(AbstractMemValue v) {
    MemDouble other = (MemDouble) v;
    chunk.doubleData[pointer] = other.chunk.doubleData[other.pointer];
  }

  public double getDouble() {
    return chunk.doubleData[pointer];
  }


  public String toString() {
    return Double.toString(getDouble());
  }
}
