package thebeast.nodmem.value;

import thebeast.nod.type.ArrayType;
import thebeast.nod.value.*;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemDim;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.type.AbstractMemType;
import thebeast.nodmem.type.MemArrayType;

import java.util.Iterator;

/**
 * @author Sebastian Riedel
 */
public class MemArray extends AbstractMemValue<ArrayType> implements ArrayValue {

  private MemChunk chunk;
  private MemVector pointer;
  private AbstractMemType instanceType;
  private MemArrayType arrayType = (MemArrayType) type;
  private MemDim dim = ((AbstractMemType)arrayType.instanceType()).getDim();

  public MemArray(MemChunk chunk, MemVector pointer, MemArrayType type) {
    super(type);
    this.chunk = chunk;
    this.pointer = pointer;
    instanceType = (AbstractMemType) type.instanceType();
  }


  public void acceptValueVisitor(ValueVisitor visitor) {
    visitor.visitArray(this);
  }

  public Iterator<? extends Value> values() {
    return iterator();
  }

  public int size() {
    return chunk.getSize();
  }

  public Value element(int index) {
    MemVector p = new MemVector(index, dim);
    return instanceType.valueFromChunk(chunk, p);
  }

  public IntValue intElement(int index) {
    return (IntValue) element(index);
  }

  public CategoricalValue categoricalElement(int index) {
    return (CategoricalValue) element(index);
  }

  public BoolValue boolElement(int index) {
    return (BoolValue) element(index);
  }

  public DoubleValue doubleElement(int index) {
    return (DoubleValue) element(index);
  }

  public Iterator<Value> iterator() {
    final int size = chunk.getSize();
    return new Iterator<Value>() {
      private MemVector current = new MemVector(pointer);
      private int row = 0;

      public boolean hasNext() {
        return row < size;
      }

      public Value next() {
        MemVector p = new MemVector(current);
        ++row;
        current.add(dim);
        return instanceType.valueFromChunk(chunk, p);
      }

      public void remove() {

      }
    };
  }

  public MemChunk getChunk() {
    return chunk;
  }

  public MemVector getPointer() {
    return pointer;
  }

  public void copyFrom(AbstractMemValue v) {

  }

  public MemChunk chunk() {
    return chunk;
  }

  public void ensure(int size) {
    if (chunk.getCapacity() < size) chunk.increaseCapacity(size - chunk.getCapacity());
  }


  public void clear() {
    chunk.setSize(0);
  }
}
