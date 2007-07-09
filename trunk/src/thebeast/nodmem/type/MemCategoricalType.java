package thebeast.nodmem.type;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.type.TypeVisitor;
import thebeast.nod.value.CategoricalValue;
import thebeast.nod.value.Value;
import thebeast.nod.exception.NoDValueNotInTypeException;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.mem.MemDim;
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
  private HashMap<Integer, String> unknownIndices;
  private HashMap<String, Integer> unknownWords;
  private boolean storeUnknowns = false;
  private boolean unknowns;

  public MemCategoricalType(Name name, boolean unknowns, List<String> representations) {
    super(name, DataType.INT);
    setDim(1,0,0);
    //setNumIntCols(1);
    this.representations = new ArrayList<String>(representations);
    this.unknownIndices = new HashMap<Integer, String>();
    this.unknownWords = new HashMap<String, Integer>();
    indices = new HashMap<String, Integer>();
    int index = 0;
    for (String rep : representations)
      indices.put(rep, index++);
    this.unknowns = unknowns;
  }

  public String representation(int index) {
    if (unknowns && index < 0) return unknownWord(index);
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
    MemChunk chunk = new MemChunk(1, 1, MemDim.INT_DIM);
    Integer integer = indices.get(representation);
    if (integer == null && !unknowns) throw new NoDValueNotInTypeException(this, representation);
    chunk.intData[0] = integer == null ? unknownIndex(representation) : integer;
    return new MemCategorical(chunk, 0, this);
  }

  private String unknownWord(int index) {
    return UNKNOWN_REP;
//    String rep = unknownIndices.get(index);
//    return rep == null ? UNKNOWN_REP : "U:" + rep;
  }

  private int unknownIndex(String rep) {
    return -1;
//    Integer index = unknownWords.get(rep);
//    if (index == null){
//      int result = - unknownWords.size() - 1;
//      unknownWords.put(rep,result);
//      unknownIndices.put(result,rep);
//      return result;
//    } else
//      return index;
  }

  public CategoricalValue value(int index) {
    MemChunk chunk = new MemChunk(1, 1, MemDim.INT_DIM);
    chunk.intData[0] = index;
    return new MemCategorical(chunk, 0, this);
  }

  public int index(String represenation) {
    Integer index = indices.get(represenation);
    return index == null ? unknownIndex(represenation) : index;
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

  public void valueToChunk(Object value, MemChunk chunk, MemVector pointer) {
    chunk.intData[pointer.xInt] = index((String)value);
  }

  public void load(StreamTokenizer src, MemChunk dst, MemVector ptr) throws IOException {
    src.nextToken();
    String s = src.ttype == '"' || src.ttype == '\'' ? "\"" + src.sval + "\"" : src.sval;
    Integer integer = indices.get(s);
    System.out.println(name + " " + s);
    if (!unknowns && integer == null)
      throw new RuntimeException(this + " has no value " + s);
    dst.intData[ptr.xInt] = integer == null ? -1 : integer;
  }

  public void load(String s, MemChunk dst, MemVector ptr) throws IOException {
    Integer integer = indices.get(s);
    //System.out.println(name + " " + s);
    if (!unknowns && integer == null)
      throw new RuntimeException(this + " has no value " + s);
    dst.intData[ptr.xInt] = integer == null ? -1 : integer;
  }

}

