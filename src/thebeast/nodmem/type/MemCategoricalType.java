package thebeast.nodmem.type;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.type.TypeVisitor;
import thebeast.nod.value.CategoricalValue;
import thebeast.nod.value.Value;
import thebeast.nod.exception.NoDValueNotInTypeException;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.value.MemCategorical;
import thebeast.nodmem.value.AbstractMemValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class MemCategoricalType extends AbstractScalarType implements CategoricalType {


  private ArrayList<String> representations;
  private HashMap<String, Integer> indices;
  private boolean unknowns;

  public MemCategoricalType(Name name, boolean unknowns, List<String> representations) {
    super(name, DataType.INT);
    setNumIntCols(1);
    this.representations = new ArrayList<String>(representations);
    indices = new HashMap<String, Integer>();
    int index = 0;
    for (String rep : representations)
      indices.put(rep, index++);
    this.unknowns = unknowns;
  }

  public String representation(int index) {
    if (unknowns && index == -1) return UNKNOWN_REP;
    return representations.get(index);
  }

  public void acceptTypeVisitor(TypeVisitor visitor) {
    visitor.visitCategoricalType(this);
  }

  public List<CategoricalValue> values() {
    ArrayList<CategoricalValue> result = new ArrayList<CategoricalValue>(representations.size());
    for (int i = 0; i < representations.size(); ++i) {
      result.add(value(i));
    }
    return result;
  }

  public CategoricalValue value(String representation) {
    MemChunk chunk = new MemChunk(1, 1, 1, 0, 0);
    Integer integer = indices.get(representation);
    if (integer == null && !unknowns) throw new NoDValueNotInTypeException(this, representation);
    chunk.intData[0] = integer == null ? -1 : integer;
    return new MemCategorical(chunk, 0, this);
  }

  public CategoricalValue value(int index) {
    MemChunk chunk = new MemChunk(1, 1, 1, 0, 0);
    chunk.intData[0] = index;
    return new MemCategorical(chunk, 0, this);
  }

  public int index(String represenation) {
    Integer index = indices.get(represenation);
    return index == null ? -1 : index;
  }

  public boolean unknowns() {
    return unknowns;
  }

  public Value emptyValue() {
    return value(0);
  }

  public AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer) {
    return new MemCategorical(chunk, pointer.xInt, this);
  }

  public void load(StreamTokenizer src, MemChunk dst, MemVector ptr) throws IOException {
    src.nextToken();
    String s = src.ttype == '"' || src.ttype == '\'' ? "\"" + src.sval + "\"" : src.sval;
    Integer integer = indices.get(s);
    if (!unknowns && integer == null)
      throw new RuntimeException(this + " has no value " + s);
    dst.intData[ptr.xInt] = integer == null ? -1 : integer;
  }

}

