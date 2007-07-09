package thebeast.nodmem.type;

import thebeast.nod.type.TupleType;
import thebeast.nod.type.TypeVisitor;
import thebeast.nod.type.Attribute;
import thebeast.nod.value.Value;
import thebeast.nod.value.TupleValue;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.mem.MemPointer;
import thebeast.nodmem.value.MemTuple;
import thebeast.nodmem.value.AbstractMemValue;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemTupleType extends AbstractMemType implements TupleType {

  private MemHeading heading;

  public MemTupleType(MemHeading heading) {
    this.heading = heading;
    setDim(0, 0, 1);
    //setNumChunkCols(1);
  }

  public void acceptTypeVisitor(TypeVisitor visitor) {
    visitor.visitTupleType(this);
  }

  public MemHeading heading() {
    return heading;
  }

  public TupleValue tuple(List<Value> values) {
    return null;
  }

  //todo:
  public TupleValue tuple(Value... values) {
    return null;
  }

  public MemTuple emptyTuple() {
    return new MemTuple(
            new MemChunk(1, 1, heading.getDim()),
            new MemVector(), this);
  }

  public Value emptyValue() {
    return emptyTuple();
  }

  public AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemTuple(chunk.chunkData[pointer.xChunk], new MemVector(), this);
  }

  public void valueToChunk(Object value, MemChunk chunk, MemVector pointer) {
    Object[] array = (Object[]) value;
    MemChunk dst = chunk.chunkData[pointer.xChunk];
    MemVector dstPointer = new MemVector();
    for (int index = 0; index < heading.attributes().size();++index){
      dstPointer.set(0,0,0);
      dstPointer.add(heading.pointerForIndex(index));
      ((AbstractMemType)heading.attributes().get(index)).valueToChunk(array[index],dst,dstPointer);
    }
  }

  public void valueToChunkWithOffset(Object value, MemChunk chunk, MemVector pointer) {
     Object[] array = (Object[]) value;
     MemChunk dst = chunk;//chunk.chunkData[pointer.xChunk];
     MemVector dstPointer = new MemVector();
     for (int index = 0; index < heading.attributes().size();++index){
       dstPointer.set(pointer);
       dstPointer.add(heading.pointerForIndex(index));
       ((AbstractMemType)heading.attributes().get(index).type()).valueToChunk(array[index],dst,dstPointer);
     }
   }



  public MemTuple tupleFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemTuple(chunk, pointer, this);
  }

  public String toString() {
    return "TUPLE {" + heading + "}";
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemTupleType type = (MemTupleType) o;

    return heading.equals(type.heading);

  }

  public int hashCode() {
    return heading.hashCode();
  }
}
