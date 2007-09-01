package thebeast.pml;

import thebeast.nod.expression.BoolExpression;
import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.Summarize;
import thebeast.nod.statement.AttributeAssign;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.RelationUpdate;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.type.Attribute;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.IntVariable;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
import thebeast.util.HashMultiMapList;

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
          directScoreTables = new HashMap<UserPredicate, RelationVariable>();

  private HashMultiMapList<UserPredicate, RelationExpression>
          directScoreQueries = new HashMultiMapList<UserPredicate, RelationExpression>();

  private IntVariable directScoreIndex;

  private HashMap<UserPredicate, RelationVariable>
          atomScores = new HashMap<UserPredicate, RelationVariable>();

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private LocalFeatures localFeatures;

  private HashMap<UserPredicate, RelationExpression>
          queries = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          sums = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          directScoreSums = new HashMap<UserPredicate, RelationExpression>();

  private HashMap<UserPredicate, RelationUpdate>
          penalizeCorrects = new HashMap<UserPredicate, RelationUpdate>(),
          encourageInCorrects = new HashMap<UserPredicate, RelationUpdate>();

  private GroundAtoms gold, closure, localAtoms;

  public Scores(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    this.signature = model.getSignature();
    localFeatures = new LocalFeatures(model, weights);
    gold = signature.createGroundAtoms();
    localAtoms = signature.createGroundAtoms();
    closure = signature.createGroundAtoms();
    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    directScoreIndex = interpreter.createIntVariable(builder.num(weights.getFeatureCount()).getInt());
    for (UserPredicate predicate : model.getHiddenPredicates()) {

      RelationVariable directScoreTable = interpreter.createRelationVariable(predicate.getHeadingArgsIndexScore());
      directScoreTables.put(predicate, directScoreTable);
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

      //for direct scores
      builder.expr(directScoreTable);
      for (int i = 0; i < predicate.getArgumentTypes().size(); ++i) {
        builder.by(predicate.getColumnName(i));
      }
      builder.doubleAttribute("score");
      builder.summarizeAs("score", Summarize.Spec.DOUBLE_SUM).summarize();
      directScoreSums.put(predicate, builder.getRelation());

      //for grouped features
      builder.expr(localFeatures.getGroupedRelation(predicate)).from("features");
      for (Attribute attribute : predicate.getHeading().attributes()) {
        builder.id(attribute.name()).attribute("features", attribute);
      }
      builder.id("score");
      builder.expr(weights.getWeights());
      builder.attribute("features", UserPredicate.getFeatureIndicesAttribute());
      builder.indexedSum("index");
      builder.tuple(predicate.getArity() + 1);
      builder.select().query();
      sums.put(predicate, builder.getRelation());

      //for add losses
      builder.expr(gold.getGroundAtomsOf(predicate).getRelationVariable());
      for (Attribute att : predicate.getHeading().attributes())
        builder.id(att.name()).attribute(att);
      builder.tupleForIds();
      builder.contains();
      BoolExpression whereGold = builder.getBool();
      StatementFactory statementFactory = TheBeast.getInstance().getNodServer().statementFactory();
      DoubleExpression minus1 = builder.doubleAttribute("score").num(-1.0).doubleAdd().getDouble();
      AttributeAssign substract = statementFactory.createAttributeAssign("score", minus1);
      RelationUpdate penalize = statementFactory.createRelationUpdate(scores, whereGold, substract);
      penalizeCorrects.put(predicate, penalize);

      BoolExpression whereNotGold = builder.expr(whereGold).not().getBool();
      DoubleExpression plus1 = builder.doubleAttribute("score").num(1.0).doubleAdd().getDouble();
      AttributeAssign add = statementFactory.createAttributeAssign("score", plus1);
      RelationUpdate encourage = statementFactory.createRelationUpdate(scores, whereNotGold, add);
      encourageInCorrects.put(predicate, encourage);
    }

    QueryGenerator generator = new QueryGenerator();
    for (FactorFormula formula : model.getDirectScoreFormulas()) {
      RelationExpression expr = generator.generateDirectLocalScoreQuery(formula, directScoreIndex, localAtoms, weights);
      directScoreQueries.add(formula.getLocalPredicate(), expr);
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

  public Weights getWeights() {
    return weights;
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
    closure.load(scores.getClosure());
  }

  public RelationVariable getScoreRelation(UserPredicate predicate) {
    return atomScores.get(predicate);
  }


  /**
   * Scores a set of ground atoms using the one-feature-per-row represention of the local features
   *
   * @param features    the local features active for each ground atom
   * @param observation the observation
   */
  public void score(LocalFeatures features, GroundAtoms observation) {
    //todo: deal with cases where we have both direct scores and weights
    localFeatures.load(features);
    interpreter.assign(directScoreIndex, this.weights.getFeatureCounter());
    localAtoms.load(observation);
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      if (directScoreQueries.containsKey(predicate)) {
        RelationVariable indexAndScore = directScoreTables.get(predicate);
        interpreter.clear(indexAndScore);
        for (RelationExpression query : directScoreQueries.get(predicate)) {
          interpreter.insert(indexAndScore, query);
        }
        interpreter.assign(atomScores.get(predicate), directScoreSums.get(predicate));
      } else {
        interpreter.assign(atomScores.get(predicate), queries.get(predicate));
      }
    }
    closure.load(localFeatures.getClosure());
  }

  /**
   * Scores a set of ground atoms using the one-atom-per-row represention of the local features
   *
   * @param features    the local features active for each ground atom, one-atom-per-row (group view) must be valid.
   * @param observation the observation
   */
  public void scoreWithGroups(LocalFeatures features, GroundAtoms observation) {
    localFeatures.load(features);
    interpreter.assign(directScoreIndex, this.weights.getFeatureCounter());
    localAtoms.load(observation);
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      if (directScoreQueries.containsKey(predicate)) {
        RelationVariable indexAndScore = directScoreTables.get(predicate);
        interpreter.clear(indexAndScore);
        for (RelationExpression query : directScoreQueries.get(predicate)) {
          interpreter.insert(indexAndScore, query);
        }
        interpreter.assign(atomScores.get(predicate), directScoreSums.get(predicate));
      } else {
        interpreter.assign(atomScores.get(predicate), sums.get(predicate));
      }
    }
    //closure.load(features.getClosure());
  }

  public GroundAtoms getClosure() {
    return closure;
  }

  public void clear() {
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      interpreter.clear(atomScores.get(predicate));
    }
  }

  public void penalize(GroundAtoms gold) {
    this.gold.load(gold, model.getHiddenPredicates());
    //add 1 to each wrong ground atom, add -1 to each corrent one.
    for (RelationUpdate update : penalizeCorrects.values()) {
      interpreter.interpret(update);
    }
    for (RelationUpdate update : encourageInCorrects.values()) {
      interpreter.interpret(update);
    }
  }


}
