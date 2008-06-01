package thebeast.nodmem.value;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.TupleType;
import thebeast.nod.value.*;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.type.AbstractMemType;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemTupleType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemTuple extends AbstractMemValue<TupleType> implements TupleValue {

  private MemChunk chunk;
  private MemVector pointer;
  private MemHeading heading;
  private int size;

  public MemTuple(MemChunk chunk, MemVector pointer, MemTupleType type) {
    super(type);
    this.chunk = chunk;
    this.pointer = pointer;
    heading = type.heading();
    size = heading.attributes().size();
  }

  public void acceptValueVisitor(ValueVisitor visitor) {
    visitor.visitTuple(this);
  }

  public List<? extends Value> values() {
    ArrayList<Value> result = new ArrayList<Value>();
    for (Attribute attribute : heading.attributes()) {
      AbstractMemType type = (AbstractMemType) attribute.type();
      MemVector p = new MemVector(pointer);
      p.add(new MemVector(heading.pointerForAttribute(attribute.name())));
      result.add(type.valueFromChunk(chunk, new MemVector(p)));
      //p.add(type.getNumIntCols(), type.getNumDoubleCols(), type.getNumChunkCols());
    }
    return result;
  }

  public Value element(String name) {
    MemVector p = new MemVector(pointer);
    p.add(heading.pointerForAttribute(name));
    return ((AbstractMemType) heading.getType(name)).valueFromChunk(chunk, p);
  }

  public Value element(int index) {
    MemVector p = new MemVector(pointer);
    p.add(heading.pointerForIndex(index));
    return ((AbstractMemType) heading.attributes().get(index).type()).valueFromChunk(chunk, p);
  }

  public IntValue intElement(String name) {
    return (IntValue) element(name);
  }

  public IntValue intElement(int index) {
    return (IntValue) element(index);
  }


  public CategoricalValue categoricalElement(int index) {
    return (CategoricalValue) element(index);
  }

  public RelationValue relationElement(int index) {
    return (RelationValue) element(index);
  }

  public RelationValue relationElement(String name) {
    return (RelationValue) element(name);
  }

  public int size() {
    return size;
  }

  public DoubleValue doubleElement(int index) {
    return (DoubleValue) element(index);
  }

  public DoubleValue doubleElement(String name) {
    return (DoubleValue) element(name);
  }

  public void copyFrom(AbstractMemValue v) {
    MemTuple other = (MemTuple) v;
    System.arraycopy(
            other.chunk.intData, other.pointer.xInt,
            chunk.intData, pointer.xInt,
            heading.getNumIntCols());
    System.arraycopy(
            other.chunk.doubleData, other.pointer.xDouble,
            chunk.doubleData, pointer.xDouble,
            heading.getNumDoubleCols());
    System.arraycopy(
            other.chunk.chunkData, other.pointer.xChunk,
            chunk.chunkData, pointer.xChunk,
            heading.getNumChunkCols());
  }

  public MemChunk chunk() {
    return chunk;
  }

  public MemVector pointer() {
    return pointer;
  }
}
