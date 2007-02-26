package thebeast.pml;

import thebeast.nod.expression.AttributeExpression;
import thebeast.nod.expression.DepthFirstExpressionVisitor;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.PredicateAtom;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.function.WeightFunction;
import thebeast.util.HashMultiMap;

import java.util.HashSet;

/**
 * The LocalFeatureExtractor can take some {@link thebeast.pml.GroundAtoms} and extract
 * a set of {@link thebeast.pml.LocalFeatures}. This functionality is taken out of the features
 * object because there might be many local feature objects (cached during training) and
 * the all use the same queries.
 */
public class LocalFeatureExtractor {

  private HashMultiMap<UserPredicate, RelationExpression>
          queries = new HashMultiMap<UserPredicate, RelationExpression>();
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private Model model;
  private Weights weights;

  private GroundAtoms atoms;

  public LocalFeatureExtractor(Model model, Weights weights) {
    configure(model, weights);
  }

  private void configure(Model model, Weights weights) {
    atoms = model.getSignature().createGroundAtoms();
    this.weights = weights;
    this.model = model;
    QueryGenerator generator = new QueryGenerator(weights, atoms);
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

  public Model getModel() {
    return model;
  }

  public Weights getWeights() {
    return weights;
  }

  public void extract(GroundAtoms groundAtoms, LocalFeatures features) {
    atoms.load(groundAtoms);
    for (UserPredicate pred : queries.keySet())
      for (RelationExpression expression : queries.get(pred)) {
        interpreter.insert(features.getRelation(pred), expression);
      }
  }

}
