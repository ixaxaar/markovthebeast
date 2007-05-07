package thebeast.pml;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.type.Heading;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.TypeFactory;

import java.io.IOException;

/**
 * A feature vector contains three sparse vectors, one for features with free weights, one for features with
 * non-negative weights and one for features with non-positive weights.
 */
public class FeatureVector {

  private SparseVector free, nonnegative, nonpositive;

  private RelationVariable localNNIndices,localNPIndices;

  private static Heading indexHeading;

  static {
    TypeFactory typeFactory = TheBeast.getInstance().getNodServer().typeFactory();
    Attribute index = typeFactory.createAttribute("index",typeFactory.intType());
    indexHeading = typeFactory.createHeading(index);
  }

  public FeatureVector() {
    free = new SparseVector();
    nonnegative = new SparseVector();
    nonpositive = new SparseVector();
    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    localNNIndices = interpreter.createRelationVariable(indexHeading);
    localNPIndices = interpreter.createRelationVariable(indexHeading);
  }

  public SparseVector getFree() {
    return free;
  }

  public SparseVector getNonnegative() {
    return nonnegative;
  }

  public SparseVector getNonpositive() {
    return nonpositive;
  }

  public void setSignedLocalweights(Model model, Weights weights){
    int[] localWeights = getFree().getIndexArray();
    localNNIndices.assignByArray(
            weights.intersectIndices(weights.getIndices(model.getLocalNonnegativeWeightFunctions()),localWeights),null);
    localNPIndices.assignByArray(
            weights.intersectIndices(weights.getIndices(model.getLocalNonpositiveWeightFunctions()),localWeights),null);
  }

  public int[] getLocalNonnegativeIndices() {
    return localNNIndices.getIntColumn("index");
  }

  public int[] getLocalNonpositiveIndices() {
    return localNPIndices.getIntColumn("index");
  }

  public void write(FileSink sink) throws IOException {
    free.write(sink);
    nonnegative.write(sink);
    nonpositive.write(sink);
    sink.write(localNNIndices);
    sink.write(localNPIndices);
  }

  public void read(FileSource source) throws IOException {
    free.read(source);
    nonnegative.read(source);
    nonpositive.read(source);
    source.read(localNNIndices);
    source.read(localNPIndices);

  }

  public int getMemoryUsage(){
    return free.getMemoryUsage() + nonnegative.getMemoryUsage() + nonpositive.getMemoryUsage();
  }

  public void load(FeatureVector vector){
    free.load(vector.getFree());
    nonnegative.load(vector.getNonnegative());
    nonpositive.load(vector.getNonpositive());
    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    interpreter.assign(localNNIndices,vector.localNNIndices);
    interpreter.assign(localNPIndices,vector.localNPIndices);
  }

  public SparseVector getAll(){
    int[] indices = new int[size()];
    double[] values = new double[size()];
    System.arraycopy(free.getIndexArray(), 0, indices, 0, free.size());
    System.arraycopy(free.getValueArray(), 0, values, 0, free.size());
    System.arraycopy(nonnegative.getIndexArray(), 0, indices, free.size(), nonnegative.size());
    System.arraycopy(nonnegative.getValueArray(), 0, values, free.size(), nonnegative.size());
    int sizeFreeNN = free.size() + nonnegative.size();
    System.arraycopy(nonpositive.getIndexArray(), 0, indices, sizeFreeNN, nonpositive.size());
    System.arraycopy(nonpositive.getValueArray(), 0, values, sizeFreeNN, nonpositive.size());
    return new SparseVector(indices, values);

  }

  private int size() {
    return free.size() + nonnegative.size() + nonpositive.size();
  }

  public void loadSigned(FeatureVector vector) {
    this.nonnegative.load(vector.nonnegative);
    this.nonpositive.load(vector.nonpositive);
  }
}
