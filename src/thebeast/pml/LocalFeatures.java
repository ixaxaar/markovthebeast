package thebeast.pml;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
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

/**
 * LocalFeatures contain a mapping from ground atoms to feature indices. It can be used to represent all active features
 * for all possible ground atoms (and thus for scoring the ground atom).
 */
public class LocalFeatures {

  private HashMap<UserPredicate, RelationVariable>
          grouped = new HashMap<UserPredicate, RelationVariable>();
  private HashMap<UserPredicate, RelationVariable>
          features = new HashMap<UserPredicate, RelationVariable>();
  private HashMap<UserPredicate, RelationExpression>
          groupExpressions = new HashMap<UserPredicate, RelationExpression>();
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
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
      RelationVariable group = interpreter.createRelationVariable(pred.getHeadingGroupedFeatures());
      grouped.put(pred, group);
      interpreter.addIndex(group, "args", Index.Type.HASH, pred.getHeading().getAttributeNames());
      builder.expr(var).group("features", "index");
      groupExpressions.put(pred, builder.getRelation());
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
   * Call this method if you have made changes to the ungrouped
   * tables of these features (i.e. the tables in which we have
   * a row for each ground atom and each feature index active
   * for this ground atom).
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
    Object[] args = new Object[terms.length + 2];
    int index = 0;
    for (Object term : terms) {
      args[index++] = term;
    }
    args[index] = featureIndex;
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
      for (TupleValue tuple : features.get(predicate).value()) {
        int index = 0;
        result.append("for ").append(predicate.getName()).append("(");
        for (Value value : tuple.values()) {
          if (index < predicate.getArity()) {
            if (index > 0) result.append(", ");
            result.append(value);
          } else result.append(") add ").append(weights.getFeatureString(((IntValue) value).getInt()));
          index++;
        }
        result.append("\n");
      }
    }
    return result.toString();
  }


  public void clear() {
    for (RelationVariable var : features.values())
      interpreter.clear(var);
  }
}
