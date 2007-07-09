package thebeast.nodmem.type;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.IntType;
import thebeast.nod.type.TypeVisitor;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.Value;
import thebeast.nod.exception.NoDValueNotInTypeException;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.mem.MemDim;
import thebeast.nodmem.value.MemInt;
import thebeast.nodmem.value.AbstractMemValue;
import thebeast.nodmem.identifier.MemName;

import java.util.Iterator;
import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class MemIntType extends AbstractScalarType implements IntType, Iterable<IntValue> {

  private int from, to;

  public static final MemIntType INT = new MemIntType(new MemName("int"),Integer.MIN_VALUE, Integer.MAX_VALUE);

  public MemIntType(Name name, int from, int to) {
    super(name, DataType.INT);
    setDim(1,0,0);
    //setNumIntCols(1);
    this.to = to;
    this.from = from;
  }

  public void acceptTypeVisitor(TypeVisitor visitor) {
    visitor.visitIntType(this);
  }

  public int from() {
    return from;
  }

  public int to() {
    return to;
  }

  public IntValue value(int value) {
    if (value < from || value >= to) throw new NoDValueNotInTypeException(this, value);
    MemChunk chunk = new MemChunk(1, 1, MemDim.INT_DIM);
    chunk.intData[0] = value;
    return new MemInt(chunk, 0, this);
  }

  public Iterator<IntValue> iterator() {
    return new Iterator<IntValue>() {
      private int current = from;

      public boolean hasNext() {
        return current < to;
      }

      public IntValue next() {
        return value(current++);
      }

      public void remove() {

      }
    };
  }

  public Iterable<IntValue> values() {
    return this;
  }

  public int size() {
    return to - from;
  }

  public Value emptyValue() {
    return value(0);
  }

  public AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemInt(chunk, pointer.xInt, this);
  }

  public void valueToChunk(Object value, MemChunk chunk, MemVector pointer) {
    chunk.intData[pointer.xInt] = (Integer)value;
  }


  public void load(StreamTokenizer src, MemChunk dst, MemVector ptr) throws IOException {
    src.nextToken();
    dst.intData[ptr.xInt] = (int) src.nval;
  }


  public void load(String src, MemChunk dst, MemVector ptr) throws IOException {
    dst.intData[ptr.xInt] = Integer.valueOf(src);
  }
}

