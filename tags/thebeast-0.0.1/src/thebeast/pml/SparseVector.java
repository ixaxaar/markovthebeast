package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.DoubleVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.FileSink;
import thebeast.nod.FileSource;

import java.util.LinkedList;
import java.util.Arrays;
import java.io.IOException;

/**
 * A SparseVector based by a database table with index and value column.
 *
 * @author Sebastian Riedel
 */
public class SparseVector {

  private static Heading heading;
  private static Heading headingIndex;

  public static class Indices {
    private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    private RelationVariable indices =
            interpreter.createRelationVariable(headingIndex);


    public Indices() {
    }

    public Indices(int[] indexArray){
      indices.assignByArray(indexArray, new double[0]);
    }

    public void add(Indices indices){
      interpreter.insert(this.indices, indices.indices);      
    }

    public RelationVariable getRelation() {
      return indices;
    }

    public void add(int index){
      indices.addTuple(index);
    }

    public int[] getIndexArray(){
      return indices.getIntColumn("index");
    }

    public boolean contains(int index){
      return indices.contains(index);
    }

    public String toString() {
      return indices.value().toString();
    }

    public int size(){
      return indices.value().size();
    }
  }


  static {
    TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
    Attribute index = factory.createAttribute("index", factory.intType());
    Attribute value = factory.createAttribute("value", factory.doubleType());
    LinkedList<Attribute> attributes = new LinkedList<Attribute>();
    attributes.add(index);
    attributes.add(value);
    heading = factory.createHeadingFromAttributes(attributes);

    LinkedList<Attribute> attributesIndex = new LinkedList<Attribute>();
    attributesIndex.add(index);
    headingIndex = factory.createHeadingFromAttributes(attributesIndex);
  }

  private RelationVariable values, otherValues;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private RelationExpression sparseAdd;
  private RelationExpression removeZeros;
  private DoubleVariable scale;
  private ExpressionBuilder builder;


  public SparseVector() {
    values = interpreter.createRelationVariable(heading);
    interpreter.addIndex(values, "index", Index.Type.HASH, "index");
    otherValues = interpreter.createRelationVariable(heading);
    interpreter.addIndex(otherValues, "index", Index.Type.HASH, "index");
    builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    scale = interpreter.createDoubleVariable(builder.num(1.0).getDouble());

    sparseAdd = builder.expr(values).expr(scale).expr(otherValues).sparseAdd("index", "value").getRelation();
    //builder.id("index").
    //distinctOthers = builder.expr(other).expr(values).relationMinus().getRelation();
    removeZeros = builder.expr(values).doubleAttribute("value").num(0.0).equality().not().restrict().getRelation();
  }

  public SparseVector(int indices[], double[] values){
    this();
    this.values.assignByArray(indices, values);
  }

 

  public SparseVector(int indices[], double constant){
    this();
    double[] values = new double[indices.length];
    Arrays.fill(values,constant);
    this.values.assignByArray(indices, values);
  }

  public void addValue(int index, double value) {
    values.addTuple(index, value);
  }

  public Indices getIndices(){
    return new Indices(getIndexArray());
  }

  public boolean contains(int index, double value) {
    return values.contains(index, value);
  }

  public void compactify(){
    interpreter.assign(otherValues, removeZeros);
    interpreter.assign(values,otherValues);
  }

  public int nonZeroCount(){
    double[] values = getValueArray();
    int count = 0;
    for (double value : values)
      if (value != 0.0) ++count;
    return count;
  }

  public double norm(){
    double[] values = getValueArray();
    double norm = 0;
    for (double value : values)
      norm += value * value;
    return norm;
  }


  public SparseVector add(double scale, SparseVector other) {
    SparseVector result = new SparseVector();
    interpreter.assign(this.otherValues, other.values);
    interpreter.assign(this.scale, builder.num(scale).getDouble());
    interpreter.assign(result.values, sparseAdd);
    return result;
  }

  public void addInPlace(double scale, SparseVector other) {
    interpreter.assign(this.otherValues, other.values);
    interpreter.assign(this.scale, builder.num(scale).getDouble());
    interpreter.assign(values, sparseAdd);
  }

  public RelationVariable getValuesRelation() {
    return values;
  }

  public void load(SparseVector sparseVector) {
    interpreter.assign(values, sparseVector.values);
  }

  public int size() {
    return values.value().size();
  }

  public int getMemoryUsage() {
    return values.byteSize();
  }

  public SparseVector copy() {
    SparseVector result = new SparseVector();
    result.load(this);
    return result;
  }

  public String toString() {
    return values.value().toString();
  }

  public void write(FileSink fileSink) throws IOException {
    fileSink.write(values);
  }

  public void read(FileSource fileSource) throws IOException {
    fileSource.read(values);
  }

  public int[] getIndexArray() {
    return values.getIntColumn("index");
  }

  public double[] getValueArray() {
    return values.getDoubleColumn("value");
  }

  public void clear(){
    interpreter.clear(values);
    interpreter.clear(otherValues);
  }

}
