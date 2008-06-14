package thebeast.nodmem.type;

import thebeast.nod.expression.TupleComponent;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.Type;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemDim;
import thebeast.nodmem.mem.MemPointer;
import thebeast.nodmem.mem.MemVector;
import thebeast.util.Pair;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class MemHeading implements Heading {

  private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
  private HashMap<String, Type> types = new HashMap<String, Type>();
  private HashMap<String, Attribute> name2attribute = new HashMap<String, Attribute>();

  private HashMap<String, MemPointer> id2pointer;

  private ArrayList<MemPointer> pointers;

  private HashMap<String, Integer> id2index = new HashMap<String, Integer>();

  //private int numIntCols, numDoubleCols, numChunkCols;

  private MemDim dim;

  public MemHeading(List<Pair<String, Type>> attributes) {
    attributes = new ArrayList<Pair<String, Type>>(attributes);
    Collections.sort(attributes, new AttComp1());
    for (Pair<String, Type> attribute : attributes) {
      MemAttribute att = new MemAttribute(attribute);
      this.attributes.add(att);
      types.put(attribute.arg1, attribute.arg2);
      id2index.put(attribute.arg1, this.attributes.size() - 1);
      name2attribute.put(attribute.arg1,att);
    }
    calculatePointers();
  }

  private static class AttComp1  implements Comparator<Pair<String,Type>>{

    public int compare(Pair<String, Type> pair, Pair<String, Type> pair1) {
      return pair.arg1.compareTo(pair1.arg1);
    }
  }


  public MemHeading(List<? extends Attribute> attributes, int dummy) {
    attributes = new LinkedList<Attribute>(attributes);
    Collections.sort(attributes, new AttComp2());
    for (Attribute attribute : attributes) {
      this.attributes.add(attribute);
      types.put(attribute.name(), attribute.type());
      id2index.put(attribute.name(), this.attributes.size() - 1);
      name2attribute.put(attribute.name(),attribute);
    }
    calculatePointers();
  }

  private static class AttComp2  implements Comparator<Attribute>{

    public int compare(Attribute attribute, Attribute attribute1) {
      return attribute.name().compareTo(attribute1.name());
    }
  }

  public MemHeading(List<TupleComponent> components, boolean empty) {
    components = new LinkedList<TupleComponent>(components);
    Collections.sort(components,new AttComp3());
    if (!empty) for (TupleComponent c : components) {
      MemAttribute attribute = new MemAttribute(c.name(), c.expression().type());
      this.attributes.add(attribute);
      types.put(c.name(), c.expression().type());
      id2index.put(c.name(), this.attributes.size() - 1);
      name2attribute.put(c.name(),attribute);
    }
    calculatePointers();
  }

  private static class AttComp3  implements Comparator<TupleComponent>{


    public int compare(TupleComponent tupleComponent, TupleComponent tupleComponent1) {
      return tupleComponent.name().compareTo(tupleComponent1.name());
    }
  }


  public MemPointer pointerForAttribute(String name) {
    return id2pointer.get(name);
  }

  public MemPointer pointerForIndex(int index) {
    return pointers.get(index);
  }

  public List<String> getAttributeNames(){
    ArrayList<String> result = new ArrayList<String>(attributes.size());
    for (Attribute attribute : attributes)
      result.add(attribute.name());
    return result;
  }

  public Attribute attribute(String attributeName) {
    return name2attribute.get(attributeName);
  }

  private void calculatePointers() {
    id2pointer = new HashMap<String, MemPointer>();
    id2index = new HashMap<String, Integer>();
    pointers = new ArrayList<MemPointer>(attributes.size());
    MemVector p = new MemVector();
    int index = 0;
    for (Attribute attribute : attributes) {
      AbstractMemType type = (AbstractMemType) attribute.type();
      MemPointer pointer = null;
      switch (type.getDataType()) {
        case INT:
          pointer = new MemPointer(MemChunk.DataType.INT, p.xInt);
          ++p.xInt;
          break;
        case DOUBLE:
          pointer = new MemPointer(MemChunk.DataType.DOUBLE, p.xDouble);
          ++p.xDouble;
          break;
        case CHUNK:
          pointer = new MemPointer(MemChunk.DataType.CHUNK, p.xChunk);
          ++p.xChunk;
          break;
      }
      id2pointer.put(attribute.name(), pointer);
      pointers.add(pointer);
      id2index.put(attribute.name(), index++);
    }
    dim = MemDim.create(p.xInt, p.xDouble, p.xChunk);
//    numChunkCols = p.xChunk;
//    numIntCols = p.xInt;
//    numDoubleCols = p.xDouble;
  }

  public MemDim getDim() {
    return dim;
  }

  public int getNumIntCols() {
    return dim.xInt;
  }

  public int getNumDoubleCols() {
    return dim.xDouble;
  }

  public int getNumChunkCols() {
    return dim.xChunk;
  }

  public List<Attribute> attributes() {
    return attributes;
  }

  public Type getType(String name) {
    return types.get(name);
  }

  public int getIndex(String name) {
    return id2index.get(name);
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemHeading that = (MemHeading) o;

    return types.equals(that.types);

  }

  public int hashCode() {
    return types.hashCode();
  }

  public int index(String name) {
    return id2index.get(name);
  }

  public String toString(){
    StringBuffer buffer = new StringBuffer();
    int index = 0;
    for (Attribute att: attributes){
      if (index ++ > 0) buffer.append(", ");
      buffer.append(att.name() + ":" + att.type());
    }
    return buffer.toString();
  }

  
}
