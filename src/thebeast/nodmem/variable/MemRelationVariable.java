package thebeast.nodmem.variable;

import thebeast.nod.NoDServer;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.TupleType;
import thebeast.nod.value.RelationValue;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Index;
import thebeast.nodmem.expression.AbstractMemExpression;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.statement.IndexInformation;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemRelationType;
import thebeast.nodmem.value.MemRelation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemRelationVariable extends AbstractMemVariable<RelationValue,RelationType> implements RelationVariable {
  
  private LinkedList<AbstractMemExpression> dependendExpressions = new LinkedList<AbstractMemExpression>();
  private IndexInformation information = new IndexInformation();
  private boolean needsIndexing = false;
  protected LinkedList<MemRelationVariable> owners = new LinkedList<MemRelationVariable>();
  protected MemRelationVariable owns = null;
  private ExpressionBuilder builder;
  private TupleType tupleType;

  public MemRelationVariable(NoDServer server, RelationValue value) {
    super(server, value, value.type());
    ((MemRelation) value).addOwner();
    builder = new ExpressionBuilder(server);
    tupleType = server.typeFactory().createTupleType(type.heading());
  }

  public MemRelationVariable(NoDServer server ,RelationType type){
    super(server, type,new MemChunk(1,1,0,0,1));
    chunk.chunkData[0] = new MemChunk(0,0,((MemHeading)type.heading()).getDim());
    builder = new ExpressionBuilder(server);
    tupleType = server.typeFactory().createTupleType(type.heading());
  }

  public void addDependendExpression(AbstractMemExpression expression){
    dependendExpressions.add(expression);
  }

  public List<AbstractMemExpression> dependendExpressions(){
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

  public RelationValue value(){
    return new MemRelation(chunk.chunkData[0],new MemVector(), (MemRelationType) type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitRelationVariable(this);    
  }

  public String toString(){
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

  private void addOwner(MemRelationVariable var){
    assert !var.owners.contains(this);
    if (var != this) owners.add(var);
  }

  private void removeOwner(MemRelationVariable var){
    owners.remove(var);
  }

    /**
   * Own chunk exclusively.
   */
  public void own() {
    chunk.chunkData[pointer.xChunk].own();
    for (MemRelationVariable var : new ArrayList<MemRelationVariable>(owners)){
      var.own();
    }
    owners.clear();
    if (owns != null) {
      owns.removeOwner(this);
      owns = null;
    }
  }

  public void copy(AbstractMemVariable var) {
    if (this == var) return;
    MemRelationVariable other = (MemRelationVariable) var;
    if (other.owns == this) return;
    if (owns != null) owns.removeOwner(this);
    other.addOwner(this);
    chunk.chunkData[pointer.xChunk].shallowCopy(other.chunk.chunkData[other.pointer.xChunk]);
    owns = other;
  }

  public void addTuple(Object ... args) {
    Object[] relation = new Object[]{ new Object[]{args}};
    server.interpreter().insert(this,builder.value(type(), relation).getRelation());
  }

  public boolean contains(Object ...args){
    return server.interpreter().evaluateBool(builder.expr(this).value(tupleType,args).contains().getBool()).getBool();
  }

  public Index getIndex(String name) {
    return indexInformation().getIndex(name);
  }

  public void assignByArray(int[] ints, double[] doubles) {
    MemChunk target = chunk.chunkData[pointer.xChunk];
    int newSize = ints.length / target.numIntCols;
    if (target.capacity < newSize) target.increaseCapacity(newSize - target.capacity);
    System.arraycopy(ints,0,target.intData,0,ints.length);
    System.arraycopy(doubles, 0, target.doubleData, 0, doubles.length);
    target.size = newSize;
    invalidate();
  }


  public void invalidate() {
    chunk.chunkData[pointer.xChunk].rowIndexedSoFar = 0;
    chunk.chunkData[pointer.xChunk].rowIndex.clear();
    indexInformation().invalidateIndices();
  }
}
