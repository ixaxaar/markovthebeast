package thebeast.nodmem.type;

import thebeast.nod.type.TupleType;
import thebeast.nod.type.TypeVisitor;
import thebeast.nod.value.Value;
import thebeast.nod.value.TupleValue;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
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
    setNumChunkCols(1);
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
            new MemChunk(1, 1, heading.getNumIntCols(), heading.getNumDoubleCols(), heading.getNumChunkCols()),
            new MemVector(), this);
  }

  public Value emptyValue() {
    return emptyTuple();
  }

  public AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemTuple(chunk.chunkData[pointer.xChunk], new MemVector(), this);
  }

  public MemTuple tupleFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemTuple(chunk, pointer, this);
  }

  public String toString(){
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
