package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.Summarize;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.Attribute;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;

import java.io.*;
import java.util.HashMap;

/**
 * A Scores object contains a score for each ground atom (explicitely or implicitely by not storing scores equal to
 * zero).
 *
 * @author Sebastian Riedel
 */
public class Scores {

  private Model model;
  private Weights weights;
  private Signature signature;

  private HashMap<UserPredicate, RelationVariable>
          atomScores = new HashMap<UserPredicate, RelationVariable>();

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private LocalFeatures localFeatures;

  private HashMap<UserPredicate, RelationExpression> queries = new HashMap<UserPredicate, RelationExpression>();

  public Scores(Model model, Weights weights) {
    if (model.getHiddenPredicates().isEmpty())
      throw new RuntimeException("It doesn't make sense to create a Scores" +
              " object for a model with no hidden predicates");
    this.model = model;
    this.weights = weights;
    this.signature = model.getSignature();
    localFeatures = new LocalFeatures(model, weights);
    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationVariable scores = interpreter.createRelationVariable(predicate.getHeadingForScore());
      atomScores.put(predicate, scores);
      interpreter.addIndex(scores, "args", Index.Type.HASH, predicate.getHeading().getAttributeNames());
      builder.expr(localFeatures.getRelation(predicate));
      for (int i = 0; i < predicate.getArgumentTypes().size(); ++i) {
        builder.by(predicate.getColumnName(i));
      }
      builder.expr(weights.getWeights()).intAttribute("index").doubleArrayElement();
      builder.summarizeAs("score", Summarize.Spec.DOUBLE_SUM).summarize();
      queries.put(predicate, builder.getRelation());
    }
  }


  public double getScore(UserPredicate predicate, Object... args) {
    builder.expr(atomScores.get(predicate));
    int index = 0;
    for (Attribute attribute : predicate.getHeading().attributes())
      builder.attribute(attribute).constant(attribute.type(), args[index++]).equality();
    builder.and(args.length);
    builder.restrict().tupleFrom().doubleExtractComponent(UserPredicate.getScoreAttribute().name());
    return interpreter.evaluateDouble(builder.getDouble()).getDouble();
  }

  public void addScore(UserPredicate predicate, double score, Object... terms) {
    Object[] args = new Object[terms.length + 1];
    int index = 0;
    for (Object term : terms) {
      args[index++] = term;
    }
    args[index] = score;
    atomScores.get(predicate).addTuple(args);
  }

  public Model getModel() {
    return model;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    for (UserPredicate pred : atomScores.keySet()) {
      result.append(">").append(pred.getName()).append("\n");
      result.append(atomScores.get(pred).value());
    }
    return result.toString();
  }


  /**
   * Creates a solution with ground atoms whose score exceeds the given threshold.
   *
   * @param threshold the threshold to use.
   * @return a solution with all ground atoms with score > threshold.
   */
  public GroundAtoms greedySolve(double threshold) {
    GroundAtoms result = model.getSignature().createGroundAtoms();
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      String prefix = "score";
      builder.clear();
      builder.expr(atomScores.get(predicate)).from(prefix);
      builder.attribute(prefix, UserPredicate.getScoreAttribute()).doubleValue(threshold);
      builder.doubleGreaterThan().where();
      for (Attribute att : predicate.getHeading().attributes()) {
        builder.id(att.name()).attribute(prefix, att);
      }
      builder.tuple(predicate.getHeading().attributes().size()).select();
      builder.query();
      interpreter.assign(result.getGroundAtomsOf(predicate).getRelationVariable(), builder.getRelation());
    }
    return result;
  }

  /**
   * Returns true if there is the ground atom for the given predicate with the given arguments has the given score.
   *
   * @param predicate a user predicate.
   * @param score     the score where are checking for
   * @param args      the arguments
   * @return true if the score for the given ground atom is contained in this Scores object.
   */
  public boolean contains(UserPredicate predicate, double score, Object... args) {
    Object[] tuple = new Object[args.length + 1];
    System.arraycopy(args, 0, tuple, 0, args.length);
    tuple[args.length] = score;
    return getScoreRelation(predicate).contains(tuple);
  }

  public void load(String input) {
    try {
      load(new ByteArrayInputStream(input.getBytes()));
    } catch (IOException e) {
      //won't happen
    }
  }

  /**
   * Loads scores from an input stream
   *
   * @param is input stream in PML score format
   * @throws IOException if I/O goes wrong.
   */
  public void load(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuffer buffer = new StringBuffer();
    String pred = null;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      if (line.charAt(0) == '>') {
        if (pred != null) {
          UserPredicate userPredicate = (UserPredicate) signature.getPredicate(pred);
          if (userPredicate == null)
            throw new RuntimeException("The predicate " + pred + " is not part of this signature");
          RelationVariable var = getScoreRelation(userPredicate);
          interpreter.load(var, new ByteArrayInputStream(buffer.toString().getBytes()));
        }
        pred = line.substring(1);
        buffer.setLength(0);

      } else if (!line.trim().equals("")) {
        buffer.append(line).append("\n");
      }
    }
    UserPredicate userPredicate = (UserPredicate) signature.getPredicate(pred);
    RelationVariable var = getScoreRelation(userPredicate);
    interpreter.load(var, new ByteArrayInputStream(buffer.toString().getBytes()));
  }


  /**
   * Load scores from another Scores object.
   *
   * @param scores the Scores object to load from.
   */
  public void load(Scores scores) {
    for (UserPredicate predicate : atomScores.keySet())
      interpreter.assign(getScoreRelation(predicate), scores.getScoreRelation(predicate));
  }

  public RelationVariable getScoreRelation(UserPredicate predicate) {
    return atomScores.get(predicate);
  }


  public void score(LocalFeatures features, Weights weights) {
    localFeatures.load(features);
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      interpreter.assign(atomScores.get(predicate), queries.get(predicate));
    }
  }


  public void clear() {
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      interpreter.clear(atomScores.get(predicate));
    }

  }
}
