package thebeast.nodmem.type;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.BoolType;
import thebeast.nod.type.TypeVisitor;
import thebeast.nod.value.BoolValue;
import thebeast.nod.value.Value;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.mem.MemDim;
import thebeast.nodmem.value.AbstractMemValue;
import thebeast.nodmem.value.MemBool;
import thebeast.nodmem.identifier.MemName;

import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class MemBoolType extends AbstractScalarType implements BoolType {

  public static final MemBoolType BOOL = new MemBoolType(new MemName("bool"));

  public MemBoolType(Name name) {
    super(name, DataType.INT);
    setDim(1,0,0);
    //setNumIntCols(1);
  }

  public void acceptTypeVisitor(TypeVisitor visitor) {
    visitor.visitBoolType(this);
  }


  public BoolValue value(boolean value) {
    MemChunk chunk = new MemChunk(1, 1, MemDim.INT_DIM);
    chunk.intData[0] = value ? 1 : 0;
    return new MemBool(chunk, 0, this);
  }

  public Value emptyValue() {
    return value(false);
  }

  public AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemBool(chunk, pointer.xInt, this);
  }

  public void valueToChunk(Object value, MemChunk chunk, MemVector pointer) {
    chunk.intData[pointer.xInt] = (Boolean)value ? 1 : 0;
  }

  public void load(StreamTokenizer src, MemChunk dst, MemVector ptr) throws IOException {
    src.nextToken();
    dst.intData[ptr.xInt] = src.sval.equals("true") ? 1 : 0;
  }

   public void load(String src, MemChunk dst, MemVector ptr) throws IOException {
    dst.intData[ptr.xInt] = src.equals("true") ? 1 : 0;
  }
}
