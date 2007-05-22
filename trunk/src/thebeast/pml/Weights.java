package thebeast.pml;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.NoDServer;
import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.expression.Expression;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.Attribute;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.Value;
import thebeast.nod.variable.ArrayVariable;
import thebeast.nod.variable.DoubleVariable;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.function.WeightFunction;

import java.io.*;
import java.util.*;

/**
 * A Weights object corresponds to a collection of mappings for a set of weight functions. Each weight function maps its
 * input domain to some weight, as well as to some index which can be used to identify the weight without the input
 * arguments. <p/> The Weights class members which are No-D Database variables are ment to be used directly in
 * algorithms, thus they're relatively exposed. This is by design.
 */
public class Weights implements HasProperties {

  private Signature signature;
  private IntVariable counter;
  private ArrayVariable weights;

  private NoDServer nodServer = TheBeast.getInstance().getNodServer();
  private Interpreter interpreter = nodServer.interpreter();
  private ExpressionBuilder builder = new ExpressionBuilder(nodServer);
  private HashMap<WeightFunction, RelationVariable> relations = new HashMap<WeightFunction, RelationVariable>();
  private SparseVector dotProductArg;
  private DoubleExpression dotProduct;

  private int[] tmpIndices;
  private int[] tmpIndicesList;
  private boolean[] tmpSet;


  /**
   * Creates a Weights object for weight functions from the given signature.
   *
   * @param signature a signature that has to contain the functions for which this object stores the weights.
   */
  public Weights(Signature signature) {
    this.signature = signature;
    counter = interpreter.createIntVariable(builder.integer(0).getInt());
    weights = interpreter.createArrayVariable(nodServer.typeFactory().doubleType());
    for (WeightFunction function : signature.getWeightFunctions()) {
      //List<String> names = function.getHeading().getAttributeNames();
      RelationVariable relation = interpreter.createRelationVariable(function.getIndexedHeading());
      relation.setLabel(function.getName());
      relations.put(function, relation);
      //interpreter.addIndex(relation, "toIndex", Index.Type.HASH, names.subList(0, names.size() - 1));
    }

    dotProductArg = new SparseVector();
    builder.expr(weights).expr(dotProductArg.getValuesRelation()).indexedSum("index", "value");
    dotProduct = builder.getDouble();


  }

  /**
   * Returns the signature containing the types these Weights refer to.
   *
   * @return the signature of these Weights.
   */
  public Signature getSignature() {
    return signature;
  }

  /**
   * Calculates the accumulated byte size of all weights contained in this object.
   *
   * @return the amount of memory this weights object has allocated.
   */
  public int getUsedMemory() {
    int byteSize = weights.byteSize();
    for (RelationVariable var : relations.values())
      byteSize += var.byteSize();
    return byteSize;
  }

  /**
   * This object stores its weight in a No-D array variable whose corresponding array value v has weight at each index:
   * <pre>
   * v[i] = weight of feature with index i
   * </pre>
   *
   * @return a No D array variable storing the actual weights.
   */
  public ArrayVariable getWeights() {
    return weights;
  }

  /**
   * Feature functions are stored in terms of tuples in a relation which contain the argument values and a feature index
   * (which can be used to get the feature weight from {@link thebeast.pml.Weights#getWeights()}). <p/> This variable
   * can be directly changed (and should be in the case of learning algorithms as clients). Be careful to leave the
   * relations and weight arrays in a consistent state after modification.
   *
   * @param function the function we want the relation variable for.
   * @return a relation variable storing mappings from arguments to feature indices.
   */
  public RelationVariable getRelation(WeightFunction function) {
    return relations.get(function);
  }


  /**
   * Dumps these weights to disk (in binary form).
   *
   * @param sink a database file sink.
   * @throws IOException if I/O goes wrong
   */
  public void write(FileSink sink) throws IOException {
    sink.write(counter, false);
    sink.write(weights, false);
    for (WeightFunction function : signature.getWeightFunctions()) {
      sink.write(getRelation(function), false);
    }
  }

  /**
   * loads weights from disk (in binary form).
   *
   * @param source a database file source.
   * @throws IOException if i/o goes wrong.
   */
  public void read(FileSource source) throws IOException {
    source.read(counter);
    source.read(weights);
    for (WeightFunction function : signature.getWeightFunctions()) {
      source.read(getRelation(function));
    }
  }

  /**
   * Saves these weights onto the given output stream (in text format).
   *
   * @param os the outputstream to writ to.
   */
  public void save(OutputStream os) {
    for (WeightFunction function : relations.keySet()) {
      save(function, os);
    }
  }

  /**
   * Saves these weights onto the given output stream (in text format).
   *
   * @param function the weightfunction to print out.
   * @param os       the outputstream to writ to.
   */
  public void save(WeightFunction function, OutputStream os) {
    PrintStream out = new PrintStream(os);
    builder.clear();
    out.println(">" + function.getName());
    builder.expr(relations.get(function));
    builder.from("args");
    for (int argIndex = 0; argIndex < function.getArity(); ++argIndex) {
      builder.id(function.getColumnName(argIndex));
      builder.attribute("args", function.getAttributeForArg(argIndex));
    }
    builder.id("weight");
    builder.expr(weights).intAttribute("args", "index").doubleArrayElement();
    builder.tuple(function.getArity() + 1).select().query();
    RelationVariable var = interpreter.createRelationVariable(builder.getRelation());
    var.value().writeTo(out, false);
    out.println();
  }


  /**
   * Adds a mapping from the given arguments (in object form) to the given weight for the given weight function.
   *
   * @param weightFunction the function we are adding a mapping to.
   * @param weight         the weight to be mapped to
   * @param arguments      the arguments to be mapped to the weight via the weightFunction.
   */
  public void addWeight(WeightFunction weightFunction, double weight, Object... arguments) {
    RelationVariable rel = relations.get(weightFunction);
    interpreter.append(weights, builder.doubleValue(weight).array(1).getArray());
    //the index is the first column
    builder.id(weightFunction.getIndexAttribute().name()).expr(counter).intPostInc();
    int index = 0;
    //now insert the arguments into the relation variable
    for (Type type : weightFunction.getArgumentTypes()) {
      builder.id(weightFunction.getColumnName(index)).constant(type.getNodType(), arguments[index++]);
    }
    builder.tupleForIds().relation(1);
    interpreter.insert(rel, builder.getRelation());
    builder.clear();
  }

  public void addWeight(String weightFunction, double weight, Object ... arguments){
    addWeight(signature.getWeightFunction(weightFunction),weight,arguments );
  }

  /**
   * Returns the weight of the given args via the given function. If no mapping is defined, 0.0 is returned.
   *
   * @param weightFunction the weight function f
   * @param args           the arguments x
   * @return f(x) or 0.0 if no mapping is defined for x.
   */
  public double getWeight(WeightFunction weightFunction, Object... args) {
    RelationVariable rel = relations.get(weightFunction);
    //create conjunction arg_1=args[0] && arg_2=args[1] etc.
    builder.expr(rel);
    for (int argIndex = 0; argIndex < args.length; ++argIndex) {
      Attribute attribute = weightFunction.getAttributeForArg(argIndex);
      builder.attribute(attribute).constant(attribute.type(), args[argIndex]).equality();
    }
    builder.and(args.length);
    builder.restrict();
    RelationVariable tmp = interpreter.createRelationVariable(builder.getRelation());
    if (tmp.value().size() == 0) return 0.0;
    builder.expr(weights).expr(tmp).tupleFrom().intExtractComponent("index").doubleArrayElement();
    DoubleVariable weight = interpreter.createDoubleVariable(builder.getDouble());
    return weight.value().getDouble();
  }

  /**
   * Creates a No-D expression that, when evaluated, returns the weight of the given args.
   *
   * @param weightFunction the weight function we want to evaluate.
   * @param args           the arguments to use.
   * @return the weight returned by the weight function given the arguments.
   */
  public Expression getWeightExpression(WeightFunction weightFunction, Expression... args) {
    RelationVariable rel = relations.get(weightFunction);
    //create conjunction arg_1=args[0] && arg_2=args[1] etc.
    int index = 0;
    builder.expr(weights);
    builder.expr(rel);
    for (Attribute attribute : weightFunction.getIndexedHeading().attributes().subList(1, args.length + 1))
      builder.attribute(attribute).expr(args[index++]).equality();
    builder.and(args.length);
    builder.restrict();
    builder.tupleFrom().intExtractComponent("index").doubleArrayElement();
    return builder.getExpression();
  }

  /**
   * Returns the index of the feature weight specified by the given weightFunction and the arguments.
   *
   * @param weightFunction the weight function f
   * @param args           the arguments x to f
   * @return a unique number identifiying f(x).
   */
  public int getIndex(WeightFunction weightFunction, Object... args) {
    RelationVariable rel = relations.get(weightFunction);
    //create conjunction arg_1=args[0] && arg_2=args[1] etc.
    builder.expr(rel);
    for (int argIndex = 0; argIndex < args.length; ++argIndex) {
      Attribute attribute = weightFunction.getAttributeForArg(argIndex);
      builder.attribute(attribute).constant(attribute.type(), args[argIndex]).equality();
    }
    builder.and(args.length);
    builder.restrict();
    RelationVariable tmp = interpreter.createRelationVariable(builder.getRelation());
    if (tmp.value().size() == 0) return -1;
    IntVariable result = interpreter.createIntVariable(
            builder.expr(tmp).tupleFrom().intExtractComponent("index").getInt());
    return result.value().getInt();
  }

  /**
   * Returns the weight corresponding to the given feature index.
   *
   * @param featureIndex the index of weight to return
   * @return the weight corresponding to the feature index.
   */
  public double getWeight(int featureIndex) {
    if (featureIndex >= getFeatureCount()) return 0.0;
    DoubleVariable var = interpreter.createDoubleVariable(
            builder.clear().expr(weights).integer(featureIndex).doubleArrayElement().getDouble());
    return var.value().getDouble();
  }

  public String getFeatureString(int featureIndex) {
    for (Map.Entry<WeightFunction, RelationVariable> entry : relations.entrySet()) {
      builder.expr(entry.getValue());
      builder.intAttribute("index").num(featureIndex).equality().restrict();
      RelationValue rel = interpreter.evaluateRelation(builder.getRelation());
      if (rel.size() > 0) {
        StringBuffer buffer = new StringBuffer(entry.getKey().getName());
        buffer.append("(");
        int index = 0;
        for (Value value : rel.iterator().next().values()) {
          if (index >= entry.getKey().getArity()) break;
          if (index++ > 0) buffer.append(", ");
          buffer.append(value);
        }
        buffer.append(")[").append(weights.doubleValue(featureIndex)).append("]");

        return buffer.toString();
      }
    }
    return "NOT AVAILABLE";
  }


  /**
   * Returns the number of (explicit) features.
   *
   * @return the current number of features.
   */
  public int getFeatureCount() {
    return counter.value().getInt();
  }


  /**
   * The weights class uses a integer variable to create new feature indices. This method returns it.
   *
   * @return the integer variable that contains the current feature count.
   */
  public IntVariable getFeatureCounter() {
    return counter;
  }

  /**
   * Loads the index mappings and weights from another Weights object.
   *
   * @param weights the object to load the indices and weights from.
   */
  public void load(Weights weights) {
    for (WeightFunction function : relations.keySet()) {
      RelationVariable local = relations.get(function);
      RelationVariable other = weights.getRelation(function);
      interpreter.assign(local, other);
    }
    interpreter.assign(this.weights, weights.weights);
    interpreter.assign(counter, weights.counter);
  }

  /**
   * Returns a deep copy of this object.
   *
   * @return a deep copy of this object.
   */
  public Weights copy() {
    Weights copy = new Weights(signature);
    copy.load(this);
    return copy;
  }

  /**
   * Calculate the dot product.
   *
   * @param vector a sparse vector with indices < number of weights.
   * @return the dot product of this weight vector and the given sparse vector
   */
  public double dotProduct(SparseVector vector) {
    dotProductArg.load(vector);
    return interpreter.evaluateDouble(dotProduct).getDouble();
  }

  /**
   * Add the sparse vector to this weights
   *
   * @param scale   the number to scale the argument with
   * @param weights the weights to add (scaled).
   */
  public void add(double scale, SparseVector weights) {
    interpreter.sparseAdd(this.weights, weights.getValuesRelation(), builder.num(scale).getDouble(), "index", "value");
  }

  /**
   * Add the sparse vector to this weights
   *
   * @param scale    the number to scale the argument with
   * @param weights  the weights to add (scaled).
   * @param positive if true the resulting weights will be nonnegative (if the result of the addition is positive we
   *                 keep this result, if not the result is set to be zero). If false weights will be made nonpositive
   *                 in the same fashion.
   */
  public void add(double scale, SparseVector weights, boolean positive) {
    interpreter.sparseAdd(this.weights, weights.getValuesRelation(),
            builder.num(scale).getDouble(), "index", "value", positive);
  }


  /**
   * Loads the weighs from an input string in PML weight format.
   *
   * @param src an input string in PML weight format.
   */
  public void load(String src) {
    try {
      load(new ByteArrayInputStream(src.getBytes()));
    } catch (IOException e) {
      //won't happen
    }
  }

  public RelationValue getWeights(WeightFunction function, Object... args) {
    builder.expr(getRelation(function)).from("args");
    int defined = 0;
    for (int i = 0; i < args.length; ++i) {
      if (args[i] != null) {
        Attribute attribute = function.getAttributeForArg(i);
        builder.attribute("args", attribute).value(attribute.type(), args[i]).equality();
        ++defined;
      }
    }
    builder.and(defined).where();
    for (int argIndex = 0; argIndex < function.getArity(); ++argIndex) {
      builder.id(function.getColumnName(argIndex));
      builder.attribute("args", function.getAttributeForArg(argIndex));
    }
    builder.id("weight");
    builder.expr(weights).intAttribute("args", "index").doubleArrayElement();
    builder.tuple(function.getArity() + 1).select().query();

    RelationExpression query = builder.getRelation();
    System.out.println(query);
    return interpreter.evaluateRelation(query);

  }

  /**
   * Loads the weighs from an input stream in PML weight format.
   *
   * @param is an input stream in PML weight format.
   * @throws java.io.IOException if I/O goes wrong.
   */
  public void load(InputStream is) throws IOException {
    WeightFunction weightFunction = null;
    int arity = -1;
    boolean inWeights = false;
    ExpressionBuilder argBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionBuilder weightBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    int index = 0;
    int rows = 0;
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if ("".equals(line) || line.startsWith("//")) continue;
      if (line.startsWith(">")) {
        String functionName = line.substring(1).trim();
        if (inWeights) {
          argBuilder.relation(rows);
          interpreter.assign(relations.get(weightFunction), argBuilder.getRelation());
        }
        weightFunction = (WeightFunction) signature.getFunction(functionName);
        if (weightFunction == null)
          throw new RuntimeException("Function " + functionName + " does not exist in this signature!");
        arity = weightFunction.getArgumentTypes().size();
        rows = 0;
        inWeights = true;
      } else {
        if (inWeights) {
          StringTokenizer tokenizer = new StringTokenizer(line, "[ \t]");
          for (int col = 0; col < arity; ++col) {
            String token = tokenizer.nextToken();
            thebeast.nod.type.Type type = weightFunction.getAttributeForArg(col).type();
            argBuilder.id(weightFunction.getColumnName(col));
            switch (weightFunction.getArgumentTypes().get(col).getTypeClass()) {
              case CATEGORICAL_UNKNOWN:
              case CATEGORICAL:
                argBuilder.value(type, (Object) token);
                break;
              case NEGATIVE_DOUBLE:
              case POSITIVE_DOUBLE:
              case DOUBLE:
                argBuilder.value(type, Double.parseDouble(token));
                break;
              case NEGATIVE_INT:
              case POSITIVE_INT:
              case INT:
                argBuilder.value(type, Integer.parseInt(token));
                break;
            }
          }
          double weight = Double.parseDouble(tokenizer.nextToken());
          weightBuilder.doubleValue(weight);
          argBuilder.id("index").integer(index++);
          argBuilder.tuple(arity + 1);
          ++rows;
        }

      }
    }
    if (inWeights) {
      argBuilder.relation(rows);
      interpreter.assign(relations.get(weightFunction), argBuilder.getRelation());
    }
    weightBuilder.array(index);
    interpreter.assign(weights, weightBuilder.getArray());
    interpreter.assign(counter, builder.num(index).getInt());

  }

  public void clear() {
    interpreter.assign(counter, builder.num(0).getInt());
    interpreter.clear(weights);
  }


  /**
   * Finds the union of the provided index arrays and returns it.
   *
   * @param indices a set of index arrays which must contain indices in the range [0,#weights-1].
   * @return a new index array only containing all indices contained in the provided index arrays.
   */
  public synchronized int[] unionIndices(int[]... indices) {
    //System.out.println("before intersect:" + countTrue());
    initTmps();
    int size = 0;
    for (int[] indexArray : indices) {
      for (int index : indexArray) {
        if (!tmpSet[index]) {
          tmpIndicesList[size] = index;
          tmpIndices[index] = size;
          tmpSet[index] = true;
          ++size;
        }
      }
    }
    int[] result = new int[size];
    System.arraycopy(tmpIndicesList, 0, result, 0, size);
    //System.out.println("size(intersect) = " + size);
    clearTmps(size);
    //System.out.println("after intersect:" + countTrue());
    return result;
  }


  public synchronized int[] intersectIndices(int[] indices1, int[] indices2) {
    initTmps();
    int size = 0;
    for (int index : indices1) {
      tmpSet[index] = true;
    }
    for (int index : indices2) {
      if (tmpSet[index]) {
        tmpIndicesList[size] = index;
        tmpIndices[index] = size;
        ++size;
      }
    }
    int[] result = new int[size];
    System.arraycopy(tmpIndicesList, 0, result, 0, size);

    //first indices
    clearTmps(size);
    for (int index : indices1) {
      tmpSet[index] = false;
    }
    return result;
  }

  public synchronized SparseVector getSubWeights(int[] base, int[] indices) {
    initTmps();
    for (int i = 0; i < base.length; ++i) {
      int index = base[i];
      tmpIndices[index] = i;
    }

    double[] values = new double[indices.length];
    int[] rebased = new int[indices.length];
    for (int i = 0; i < indices.length; ++i) {
      int index = indices[i];
      values[i] = weights.doubleValue(index);
      rebased[i] = tmpIndices[index];
    }
    //System.out.println("base.length = " + base.length);
    clearTmps(base.length);
    //System.out.println("after sub:" + countTrue());
    return new SparseVector(rebased, values);
  }

  private void initTmps() {
    if (tmpIndices == null) {
      tmpIndices = new int[getFeatureCount()];
      tmpIndicesList = new int[getFeatureCount()];
      tmpSet = new boolean[getFeatureCount()];
    }
  }

  public synchronized List<SparseVector> add(SparseVector lhs, double scale, List<SparseVector> rhs) {
    initTmps();
    //Arrays.fill(tmpSet,0,2000,false);
    ArrayList<SparseVector> result = new ArrayList<SparseVector>(rhs.size());
    double[] lhsValues = lhs.getValueArray();
    int[] lhsIndices = lhs.getIndexArray();
    int size = 0;
    for (int index : lhsIndices) {
      tmpSet[index] = true;
      tmpIndices[index] = size;
      tmpIndicesList[size++] = index;
    }

    ArrayList<int[]> indexArrays = new ArrayList<int[]>(rhs.size());

    //System.out.println("s1:" + size);
    for (SparseVector vector : rhs) {
      int[] indices = vector.getIndexArray();
      indexArrays.add(indices);
      for (int index : indices) {
        if (!tmpSet[index]) {
          tmpSet[index] = true;
          tmpIndices[index] = size;
          tmpIndicesList[size++] = index;
        }
      }
    }
    //System.out.println("s2:" + size);

    int[] baseIndices = new int[size];
    System.arraycopy(tmpIndicesList, 0, baseIndices, 0, size);
    double[] baseLhs = new double[size];
    for (int j = 0; j < lhsIndices.length; ++j) {
      int index = lhsIndices[j];
      baseLhs[tmpIndices[index]] = lhsValues[j];
    }
    int i = 0;
    for (SparseVector vector : rhs) {
      double[] values = vector.getValueArray();
      int[] indices = indexArrays.get(i++);
      double[] dst = new double[size];
      System.arraycopy(baseLhs, 0, dst, 0, size);
      for (int j = 0; j < indices.length; ++j) {
        int index = indices[j];
        int rebased = tmpIndices[index];
        if (rebased > dst.length) {
          System.out.println("size = " + size);
          System.out.println("j = " + j);
          System.out.println("index = " + index);
          System.out.println("rebased = " + rebased);
        }
        dst[rebased] += scale * values[j];
      }
      result.add(new SparseVector(baseIndices, dst));
    }
    //System.out.println("size = " + size);
    clearTmps(size);
    //System.out.println("after add:" + countTrue());
    return result;

  }

  private void clearTmps(int size) {
    for (int index = 0; index < size; ++index) {
      tmpSet[tmpIndicesList[index]] = false;
    }
  }


  public String toString(SparseVector vector) {
    int[] indices = vector.getIndexArray();
    double[] values = vector.getValueArray();
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < indices.length; ++i) {
      Formatter formatter = new Formatter();
      formatter.format("%-6d%-50s%5.1f\n", indices[i], getFeatureString(indices[i]), values[i]);
      buffer.append(formatter.out());
    }
    return buffer.toString();
  }

  public String toString(FeatureVector vector) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Free:\n").append(toString(vector.getLocal()));
    buffer.append("NN:\n").append(toString(vector.getFalseVector()));
    buffer.append("NP:\n").append(toString(vector.getTrueVector()));
    return buffer.toString();
  }

  /**
   * Return an array of indices of the weight functions weights
   *
   * @param function the weight function
   * @return an array of indices (in unspecified order).
   */
  public int[] getIndices(WeightFunction function) {
    return relations.get(function).getIntColumn("index");
  }

  /**
   * Builds an array with the indices of all weights the provided
   * weight functions define.
   *
   * @param functions a collections of weight functions.
   * @return an array of all weight indices of the weight functions (in unspecified order).
   */
  public int[] getIndices(Collection<WeightFunction> functions) {
    int[][] tmp = new int[functions.size()][];
    int i = 0;
    int count = 0;
    for (WeightFunction function : functions) {
      tmp[i] = getIndices(function);
      count += tmp[i].length;
      ++i;
    }
    int[] result = new int[count];
    i = 0;
    for (int[] array : tmp) {
      System.arraycopy(array, 0, result, i, array.length);
      i += array.length;
    }
    return result;
  }


  public double score(SparseVector vector){
    return weights.value().dotProduct(vector.getIndexArray(), vector.getValueArray());
  }

  public double score(FeatureVector vector){
    double result = 0;
    result += score(vector.getLocal());
    result += score(vector.getTrueVector());
    result += score(vector.getFalseVector());
    return result;  
  }

  public void setProperty(PropertyName name, Object value) {

  }


  public Object getProperty(PropertyName name) {
    return null;
  }
}
