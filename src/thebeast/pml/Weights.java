package thebeast.pml;

import thebeast.nod.NoDServer;
import thebeast.nod.expression.Expression;
import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.type.*;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.*;
import thebeast.pml.function.WeightFunction;

import java.util.HashMap;
import java.util.List;
import java.io.*;

/**
 * A Weights object corresponds to a collection of mappings for a set of weight functions. Each weight function maps its
 * input domain to some weight, as well as to some index which can be used to identify the weight without the input
 * arguments. <p/> The Weights class members which are No D Database variables are ment to be used directly in
 * algorithms, thus they're relatively exposed. This is by design.
 */
public class Weights {

  private Signature signature;
  private IntVariable sequence;
  private ArrayVariable weights;

  private NoDServer nodServer = TheBeast.getInstance().getNodServer();
  private Interpreter interpreter = nodServer.interpreter();
  private ExpressionBuilder builder = new ExpressionBuilder(nodServer);
  private HashMap<WeightFunction, RelationVariable> relations = new HashMap<WeightFunction, RelationVariable>();
  private SparseVector dotProductArg;
  private DoubleExpression dotProduct;


  /**
   * Creates a Weights object for weight functions from the given signature.
   *
   * @param signature a signature that has to contain the functions for which this object stores the weights.
   */
  public Weights(Signature signature) {
    this.signature = signature;
    sequence = interpreter.createIntVariable(builder.integer(0).getInt());
    weights = interpreter.createArrayVariable(nodServer.typeFactory().doubleType());
    for (WeightFunction function : signature.getWeightFunctions()) {
      List<String> names = function.getHeading().getAttributeNames();
      RelationVariable relation = interpreter.createRelationVariable(function.getHeading());
      relation.setLabel(function.getName());
      relations.put(function, relation);
      //interpreter.addIndex(relation, "toIndex", Index.Type.HASH, names.subList(0, names.size() - 1));
    }

    dotProductArg = new SparseVector();
    builder.expr(weights).expr(dotProductArg.getValues()).indexedSum("index", "value");
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
    int byteSize = 0;
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
   * Saves these weights onto the given output stream
   *
   * @param os the outputstream to writ to.
   */
  public void save(OutputStream os) {
    for (WeightFunction function : relations.keySet()) {
      save(function, os);
    }
  }

  /**
   * Saves these weights onto the given output stream
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
    builder.id(weightFunction.getIndexAttribute().name()).expr(sequence).intPostInc();
    int index = 0;
    //now insert the arguments into the relation variable
    for (Type type : weightFunction.getArgumentTypes()) {
      builder.id(weightFunction.getColumnName(index)).constant(type.getNodType(), arguments[index++]);
    }
    builder.tupleForIds().relation(1);
    interpreter.insert(rel, builder.getRelation());
    builder.clear();
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
    for (Attribute attribute : weightFunction.getHeading().attributes().subList(1, args.length + 1))
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

  /**
   * Returns the number of features.
   *
   * @return the current number of features.
   */
  public int getFeatureCount() {
    return sequence.value().getInt();
  }


  /**
   * The weights class uses a integer variable to create new feature indices. This method returns it.
   *
   * @return the integer variable that contains the current feature count.
   */
  public IntVariable getFeatureCounter() {
    return sequence;
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
    interpreter.assign(sequence, weights.sequence);
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
    interpreter.sparseAdd(this.weights, weights.getValues(), builder.num(scale).getDouble(), "index", "value");
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

  /**
   * Loads the weighs from an input stream in PML weight format.
   *
   * @param is an input stream in PML weight format.
   * @throws java.io.IOException if I/O goes wrong.
   */
  public void load(InputStream is) throws IOException {
    StreamTokenizer tokenizer = new StreamTokenizer(new InputStreamReader(is));
    tokenizer.parseNumbers();
    tokenizer.quoteChar('"');
    tokenizer.quoteChar('\'');
    tokenizer.whitespaceChars(' ', ' ');
    tokenizer.whitespaceChars('\t', '\t');
    tokenizer.whitespaceChars('\n', '\n');
    WeightFunction weightFunction = null;
    int arity = -1;
    boolean inWeights = false;
    ExpressionBuilder argBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionBuilder weightBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    int index = 0;
    int col = 0;
    int rows = 0;
    while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
      if (tokenizer.ttype == '>') {
        tokenizer.nextToken();
        String token = tokenizer.sval;
        if (inWeights) {
          argBuilder.relation(rows);
          interpreter.assign(relations.get(weightFunction), argBuilder.getRelation());
        }
        weightFunction = (WeightFunction) signature.getFunction(token);
        if (weightFunction == null)
          throw new RuntimeException("Function " + token + " does not exist in this signature!");
        arity = weightFunction.getArgumentTypes().size();
        rows = 0;
        inWeights = true;
      } else if (tokenizer.ttype != StreamTokenizer.TT_NUMBER && tokenizer.sval.trim().length() == 0) {
        if (inWeights) {
          argBuilder.relation(rows);
          interpreter.assign(relations.get(weightFunction), argBuilder.getRelation());
        }
        inWeights = false;
      } else {
        if (inWeights) {
          if (col < arity) {
            thebeast.nod.type.Type type = weightFunction.getAttributeForArg(col).type();
            argBuilder.id(weightFunction.getColumnName(col));
            switch (weightFunction.getArgumentTypes().get(col).getTypeClass()) {
              case CATEGORICAL_UNKNOWN:
              case CATEGORICAL:
                Object value = tokenizer.ttype == '"' ?
                        tokenizer.sval.substring(1, tokenizer.sval.length() - 1) :
                        tokenizer.sval;
                argBuilder.value(type, value);
                break;
              case NEGATIVE_DOUBLE:
              case POSITIVE_DOUBLE:
              case DOUBLE:
                argBuilder.value(type, tokenizer.nval);
                break;
              case NEGATIVE_INT:
              case POSITIVE_INT:
              case INT:
                argBuilder.value(type, (int) tokenizer.nval);
                break;
            }
            ++col;
          } else {
            double weight = tokenizer.nval;
            weightBuilder.doubleValue(weight);
            argBuilder.id("index").integer(index++);
            argBuilder.tuple(arity + 1);
            col = 0;
            ++rows;
          }
        }
      }

    }
    if (inWeights) {
      argBuilder.relation(rows);
      interpreter.assign(relations.get(weightFunction), argBuilder.getRelation());
    }
    weightBuilder.array(index);
    interpreter.assign(weights, weightBuilder.getArray());
    interpreter.assign(sequence, builder.num(index).getInt());

  }
}
