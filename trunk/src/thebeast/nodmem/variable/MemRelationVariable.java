package thebeast.nodmem.variable;

import thebeast.nod.NoDServer;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.type.*;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.RelationValue;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;
import thebeast.nodmem.expression.AbstractMemExpression;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemDim;
import thebeast.nodmem.mem.MemPointer;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.statement.IndexInformation;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemRelationType;
import thebeast.nodmem.value.MemRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemRelationVariable extends AbstractMemVariable<RelationValue, RelationType> implements RelationVariable {

  private ArrayList<AbstractMemExpression> dependendExpressions = new ArrayList<AbstractMemExpression>();
  private IndexInformation information = new IndexInformation();
  private boolean needsIndexing = false;
  protected ArrayList<MemRelationVariable> owners = new ArrayList<MemRelationVariable>();
  protected MemRelationVariable owns = null;
  private ExpressionBuilder builder;
  private TupleType tupleType;
  private static final int OVERHEAD = 3000;

  public MemRelationVariable(NoDServer server, RelationValue value) {
    super(server, value, value.type());
    ((MemRelation) value).addOwner();
    builder = server.expressionBuilder();
    //builder = new ExpressionBuilder(server);
    tupleType = server.typeFactory().createTupleType(type.heading());
  }

  public MemRelationVariable(NoDServer server, RelationType type) {
    super(server, type, new MemChunk(1, 1, 0, 0, 1));
    chunk.chunkData[0] = new MemChunk(0, 0, ((MemHeading) type.heading()).getDim());
    builder = new ExpressionBuilder(server);
    tupleType = server.typeFactory().createTupleType(type.heading());
  }

  public void addDependendExpression(AbstractMemExpression expression) {
    dependendExpressions.add(expression);
  }

  public List<AbstractMemExpression> dependendExpressions() {
    return dependendExpressions;
  }

  public IndexInformation getInformation() {
    return information;
  }

  public boolean needsIndexing() {
    return needsIndexing;
  }

  public void setNeedsIndexing(boolean needsIndexing) {
    this.needsIndexing = needsIndexing;
  }

  public RelationValue value() {
    return new MemRelation(chunk.chunkData[0], new MemVector(), (MemRelationType) type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitRelationVariable(this);
  }

  public String toString() {
    return label();
  }

  public MemRelation relation() {
    return (MemRelation) value;
  }

  public void destroy() {
    ((MemRelation) value).removeOwner();
  }

  public IndexInformation indexInformation() {
    return information;
  }

  private void addOwner(MemRelationVariable var) {
    assert !var.owners.contains(this);
    if (var != this) owners.add(var);
  }

  private void removeOwner(MemRelationVariable var) {
    owners.remove(var);
  }

  /**
   * Own chunk exclusively.
   */
  public void own() {
    //if this variable is owned by others let them own themselves again
    if (owners.size() > 0) {
      for (MemRelationVariable var : new ArrayList<MemRelationVariable>(owners)) {
        var.own();
      }
      owners.clear();
    }
    //if this variable owns another variable let it own itself exclusively
    if (owns != null) {
      chunk.chunkData[pointer.xChunk].own();
      owns.removeOwner(this);
      owns = null;
    }
  }

  public boolean copy(AbstractMemVariable var) {
    if (this == var || owns == var) return false;
    MemRelationVariable other = (MemRelationVariable) var;
    if (other.owns == this) return false;
    if (owners.size() > 0) {
      for (MemRelationVariable owner : new ArrayList<MemRelationVariable>(owners)) {
        owner.own();
      }
      owners.clear();
    }
    if (owns != null) owns.removeOwner(this);
    other.addOwner(this);
    chunk.chunkData[pointer.xChunk].shallowCopy(other.chunk.chunkData[other.pointer.xChunk]);
    //lets check if we can reuse some indices
//    for (Index index: information.getIndices()){
//      MemHashIndex hashIndex = (MemHashIndex) index;
//      int myIndexNr = information.getIndexIdForAttributes(index.attributes());
//      MemHashIndex otherIndex = (MemHashIndex) other.indexInformation().getIndex(index.attributes());
//      int otherIndexNr = other.indexInformation().getIndexIdForAttributes(index.attributes());
//      if (otherIndexNr != -1){
//        hashIndex.shallowCopy(myIndexNr, otherIndex);
//        //hashIndex.useChunk(index,);
//        //hashIndex.useChunk();
//      }
//    }
    owns = other;
    return true;
  }

  public void addTuple(Object... args) {
    Object[] relation = new Object[]{new Object[]{args}};
    server.interpreter().insert(this, builder.value(type(), relation).getRelation());
  }

  public boolean contains(Object... args) {
    return server.interpreter().evaluateBool(builder.expr(this).value(tupleType, args).contains().getBool()).getBool();
  }

  public Index getIndex(String name) {
    return indexInformation().getIndex(name);
  }

  public int[] getIntColumn(String attribute) {
    Attribute att = type.heading().attribute(attribute);
    if (att == null)
      throw new IllegalArgumentException(type + " has no attribute " + attribute);
    if (!(att.type() instanceof IntType))
      throw new IllegalArgumentException(attribute + " is not an int attribute");
    MemHeading memHeading = (MemHeading) type.heading();
    MemPointer pointer = memHeading.pointerForAttribute(attribute);
    MemDim dim = memHeading.getDim();
    MemChunk chunk = this.chunk.chunkData[this.pointer.xChunk];
    int[] result = new int[chunk.size];
    if (dim.xInt == 1)
      System.arraycopy(chunk.intData,0,result,0,chunk.size);
    else {
      int index = pointer.pointer;
      for (int row = 0;row < chunk.size; ++row, index+=dim.xInt)
        result[row] = chunk.intData[index];
    }
    return result;
  }

  public double[] getDoubleColumn(String attribute) {
    Attribute att = type.heading().attribute(attribute);
    if (!(att.type() instanceof DoubleType))
      throw new IllegalArgumentException(attribute + " is not an double attribute");
    MemHeading memHeading = (MemHeading) type.heading();
    MemPointer pointer = memHeading.pointerForAttribute(attribute);
    MemDim dim = memHeading.getDim();
    MemChunk chunk = this.chunk.chunkData[this.pointer.xChunk];
    double[] result = new double[chunk.size];
    if (dim.xInt == 1)
      System.arraycopy(chunk.doubleData,0,result,0,chunk.size);
    else {
      int index = pointer.pointer;
      for (int row = 0;row < chunk.size; ++row, index+=dim.xDouble)
        result[row] = chunk.doubleData[index];
    }
    return result;
  }

  public void assignByArray(int[] ints, double[] doubles) {
    MemChunk target = chunk.chunkData[pointer.xChunk];
    int newSize = ints.length / target.numIntCols;
    if (target.capacity < newSize) target.increaseCapacity(newSize - target.capacity);
    System.arraycopy(ints, 0, target.intData, 0, ints.length);
    if (doubles != null) System.arraycopy(doubles, 0, target.doubleData, 0, doubles.length);
    target.size = newSize;
    invalidate();
  }

  public int byteSize() {
    int size = OVERHEAD;
    size+= chunk.chunkData[pointer.xChunk].byteSize();
    return size;
  }

  public boolean hasIndex(String name) {
    return indexInformation().getIndex(name) != null;
  }


  public void invalidate() {
    chunk.chunkData[pointer.xChunk].rowIndexedSoFar = 0;
    if (chunk.chunkData[pointer.xChunk].rowIndex != null) chunk.chunkData[pointer.xChunk].rowIndex.clear();
    indexInformation().invalidateIndices();
  }
}
