package thebeast.pml;

import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.DepthFirstExpressionVisitor;
import thebeast.nod.expression.AttributeExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.PredicateAtom;
import thebeast.pml.function.WeightFunction;
import thebeast.util.HashMultiMap;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 08-Feb-2007 Time: 21:00:32
 */
public class LocalFeatures {

  private HashMap<UserPredicate, RelationVariable> features = new HashMap<UserPredicate, RelationVariable>();
  private HashMultiMap<UserPredicate, RelationExpression>
          queries = new HashMultiMap<UserPredicate, RelationExpression>();
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private Model model;
  private Weights weights;

  private GroundAtoms atoms;

  public LocalFeatures(Model model, Weights weights) {
    atoms = model.getSignature().createGroundAtoms();
    this.weights = weights;
    this.model = model;
    QueryGenerator generator = new QueryGenerator(weights, atoms);
    for (UserPredicate pred : model.getHiddenPredicates()) {
      RelationVariable var = interpreter.createRelationVariable(pred.getHeadingForFeatures());
      features.put(pred, var);
      interpreter.addIndex(var, "args", Index.Type.HASH, pred.getHeading().getAttributeNames());
    }
    for (FactorFormula formula : model.getLocalFactorFormulas()) {
      RelationExpression query = generator.generateLocalQuery(formula, atoms, weights);
      queries.add((UserPredicate) ((PredicateAtom) formula.getFormula()).getPredicate(), query);
      WeightFunction weightFunction = formula.getWeightFunction();
      RelationVariable relvar = weights.getRelation(weightFunction);
      if (relvar.getIndex(weightFunction.getName()) == null) {
        final HashSet<String> bound = new HashSet<String>();
        query.acceptExpressionVisitor(new DepthFirstExpressionVisitor() {
          public void visitAttribute(AttributeExpression attribute) {
            if (attribute.prefix().equals("weights"))
              bound.add(attribute.attribute().name());
          }
        });
        interpreter.addIndex(relvar, weightFunction.getName(), Index.Type.HASH, bound);
      }
    }

  }

  public void load(LocalFeatures localFeatures) {
    for (UserPredicate pred : features.keySet()) {
      interpreter.assign(features.get(pred), localFeatures.features.get(pred));
    }
  }

  public void extract(GroundAtoms groundAtoms) {
    atoms.load(groundAtoms);
    for (UserPredicate pred : queries.keySet())
      for (RelationExpression expression : queries.get(pred)) {
        interpreter.insert(features.get(pred), expression);
      }
  }

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

  public boolean containsFeature(UserPredicate predicate, int featureIndex, Object... terms) {
    return features.get(predicate).contains(toTuple(featureIndex, terms));
  }

  public RelationVariable getRelation(UserPredicate pred) {
    return features.get(pred);
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      result.append("#").append(predicate.getName()).append("\n");
      result.append(features.get(predicate).value().toString());
      result.append("\n");
    }
    return result.toString();
  }

}
