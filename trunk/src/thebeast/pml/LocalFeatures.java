package thebeast.pml;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.type.Attribute;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.Value;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * LocalFeatures contain a mapping from ground atoms to (local) feature indices. It can be used to represent all active
 * features for all possible ground atoms (and thus for scoring the ground atom).
 */
public class LocalFeatures implements HasProperties {

  private HashMap<UserPredicate, RelationVariable>
          grouped = new HashMap<UserPredicate, RelationVariable>();
  private HashMap<UserPredicate, RelationVariable>
          features = new HashMap<UserPredicate, RelationVariable>();
  private HashMap<UserPredicate, RelationExpression>
          groupExpressions = new HashMap<UserPredicate, RelationExpression>(),
          closures = new HashMap<UserPredicate, RelationExpression>();
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
  private Model model;
  private Weights weights;


  public LocalFeatures(Model model, Weights weights) {
    this.weights = weights;
    this.model = model;
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    for (UserPredicate pred : model.getHiddenPredicates()) {
      RelationVariable var = interpreter.createRelationVariable(pred.getHeadingForFeatures());
      features.put(pred, var);
      interpreter.addIndex(var, "args", Index.Type.HASH, pred.getHeading().getAttributeNames());
      if (pred.getArity() > 1) for (String attributeName : pred.getHeading().getAttributeNames()) {
        interpreter.addIndex(var, attributeName, Index.Type.HASH, attributeName);
      }
      RelationVariable group = interpreter.createRelationVariable(pred.getHeadingGroupedFeatures());
      grouped.put(pred, group);
      interpreter.addIndex(group, "args", Index.Type.HASH, pred.getHeading().getAttributeNames());
      builder.expr(var).group("features", "index","scale");
      groupExpressions.put(pred, builder.getRelation());

      builder.expr(group).from("grouped");
      for (Attribute attribute : pred.getHeading().attributes()) {
        builder.id(attribute.name()).attribute("grouped", attribute);
      }
      builder.tupleForIds().select().query();
      closures.put(pred, builder.getRelation());

    }
  }

  /**
   * Loads a features from another set of features.
   *
   * @param localFeatures the features to load from.
   */
  public void load(LocalFeatures localFeatures) {
    for (UserPredicate pred : features.keySet()) {
      interpreter.assign(features.get(pred), localFeatures.features.get(pred));
      interpreter.assign(grouped.get(pred), localFeatures.grouped.get(pred));
    }
  }


  /**
   * Call this method if you have made changes to the ungrouped tables of these features (i.e. the tables in which we
   * have a row for each ground atom and each feature index active for this ground atom).
   */
  public void invalidate() {
    for (UserPredicate pred : features.keySet()) {
      interpreter.assign(grouped.get(pred), groupExpressions.get(pred));
    }
  }

  public RelationVariable getGroupedRelation(UserPredicate predicate) {
    return grouped.get(predicate);
  }

  /**
   * Copies a set of local features. Copying is based on the underlying database engine, which might use a shallow
   * mechanism until any of objects is changed.
   *
   * @return a copy of this object.
   */
  public LocalFeatures copy() {
    LocalFeatures result = new LocalFeatures(model, weights);
    result.load(this);
    return result;
  }

  public void addFeature(UserPredicate predicate, int featureIndex, Object... terms) {
    Object[] args = toTuple(featureIndex, terms);
    features.get(predicate).addTuple(args);
  }

  private Object[] toTuple(int featureIndex, Object... terms) {
    return toTuple(featureIndex, 1.0, terms);
  }
  private Object[] toTuple(int featureIndex, double scale, Object... terms) {
    Object[] args = new Object[terms.length + 2];
    int index = 0;
    for (Object term : terms) {
      args[index++] = term;
    }
    args[index] = featureIndex;
    args[index+1] = scale;
    return args;
  }


  /**
   * Do we have for a given ground atom a feature with the given index.
   *
   * @param predicate    the predicate
   * @param featureIndex the index
   * @param terms        the ground atom arguments
   * @return true iff this object contains a mapping from the given ground atom to the given feature index.
   */
  public boolean containsFeature(UserPredicate predicate, int featureIndex, Object... terms) {
    return features.get(predicate).contains(toTuple(featureIndex, terms));
  }

  /**
   * Do we have for a given ground atom a feature with the given index.
   *
   * @param predicate    the predicate
   * @param featureIndex the index
   * @param terms        the ground atom arguments
   * @param scale        the scaling factor assiocated with the given ground atom and feature index
   * @return true iff this object contains a mapping from the given ground atom to the given feature index.
   */
  public boolean containsFeatureWithScale(UserPredicate predicate, int featureIndex, double scale, Object... terms) {
    return features.get(predicate).contains(toTuple(featureIndex, terms));
  }


  /**
   * Gets the table that stores the feature indices for all ground atoms of a given predicate.
   *
   * @param pred the predicate
   * @return the relvar that stores its feature indices.
   */
  public RelationVariable getRelation(UserPredicate pred) {
    return features.get(pred);
  }

  /**
   * Calculates a rough estimate of how much memory this object uses.
   *
   * @return approx. memory used in bytes.
   */
  public int getMemoryUsage() {
    int size = 0;
    for (RelationVariable var : grouped.values()) {
      size += var.byteSize();
    }
    return size;
  }

  /**
   * Write this set of features to a database dump. Indices are not serialized.
   *
   * @param fileSink the dump to write to.
   * @throws java.io.IOException if I/O goes wrong.
   */
  public void write(FileSink fileSink) throws IOException {
    for (UserPredicate pred : model.getHiddenPredicates())
      fileSink.write(grouped.get(pred), false);
  }

  /**
   * Reads features from a database dump.
   *
   * @param fileSource the dump to load from.
   * @throws IOException if I/O goes wrong.
   */
  public void read(FileSource fileSource) throws IOException {
    for (UserPredicate pred : model.getHiddenPredicates()) {
      fileSource.read(grouped.get(pred));
    }
  }

  /**
   * Creates a column-based string representation of all features
   *
   * @return all features of all hidden predicates in column format.
   */
  public String toString() {
    StringBuffer result = new StringBuffer();
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationValue value = grouped.get(predicate).value();
      result.append(">").append(predicate.getName()).append(":").append(value.size()).append("\n");
      result.append(value.toString());
      result.append("\n");
    }
    return result.toString();
  }

  /**
   * Returns a string that also displays the weight function and its argument for each ground atom.
   *
   * @return all ground atoms that have active features together with these features in literal form.
   */
  public String toVerboseString() {
    StringBuffer result = new StringBuffer();
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationValue relation = features.get(predicate).value();
      result.append(toVerboseString(relation, predicate));
    }
    return result.toString();
  }

  private String toVerboseString(RelationValue relation, final UserPredicate predicate) {
    StringBuffer result = new StringBuffer();
    ArrayList<TupleValue> sorted = new ArrayList<TupleValue>(relation.size());
    for (TupleValue tuple : relation) sorted.add(tuple);
    Collections.sort(sorted, new Comparator<TupleValue>() {
      public int compare(TupleValue o1, TupleValue o2) {
        double weightDelta = weights.getWeight(o1.intElement(predicate.getArity()).getInt()) -
                weights.getWeight(o2.intElement(predicate.getArity()).getInt());
        return weightDelta < 0 ? 1 : weightDelta > 0 ? -1 : 0;
      }
    });
    for (TupleValue tuple : sorted) {
      int index = 0;
      result.append("for ").append(predicate.getName()).append("(");
      for (int i = 0; i < predicate.getArity(); ++i){
        if (i > 0) result.append(", ");
        result.append(tuple.element(predicate.getColumnName(i)));
      }
      result.append(") add ").append(tuple.doubleElement("scale")).append(" * ");
      result.append(weights.getFeatureString(((IntValue) tuple.intElement("index")).getInt()));
      result.append("\n");
    }
    return result.toString();
  }


  public String toVerboseString(UserPredicate predicate, Object... args) {
    builder.expr(features.get(predicate));
    int index = 0;
    int argCount = 0;
    for (Attribute att : predicate.getHeading().attributes()) {
      Object arg = args[index++];
      if (arg != null) {
        ++argCount;
        builder.attribute(att).value(att.type(), arg).equality();
      }
    }
    builder.and(argCount);
    builder.restrict();
    RelationValue relation = interpreter.evaluateRelation(builder.getRelation());
    return toVerboseString(relation, predicate);

  }

  /**
   * Removes all features.
   */
  public void clear() {
    for (RelationVariable var : features.values())
      interpreter.clear(var);
    for (RelationVariable var : grouped.values())
      interpreter.clear(var);
  }

  /**
   * Determines all hidden ground atoms which have active features
   *
   * @return all ground atoms with active features.
   */
  public GroundAtoms getClosure() {
    GroundAtoms closure = model.getSignature().createGroundAtoms();
    for (UserPredicate pred : model.getHiddenPredicates()) {
      interpreter.assign(closure.getGroundAtomsOf(pred).getRelationVariable(), closures.get(pred));
    }
    return closure;
  }


  public void setProperty(PropertyName name, Object value) {

  }


  public Object getProperty(PropertyName name) {
    UserPredicate pred = model.getSignature().getUserPredicate(name.getHead());
    if (name.hasArguments()) {
      return toVerboseString(pred, name.getArguments().toArray(new Object[0]));
    }
    return toVerboseString(features.get(pred).value(), pred);
  }
}
