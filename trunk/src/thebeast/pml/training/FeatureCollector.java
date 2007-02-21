package thebeast.pml.training;

import thebeast.pml.Model;
import thebeast.pml.Weights;
import thebeast.pml.GroundAtoms;
import thebeast.pml.TheBeast;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.corpora.Corpus;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.statement.AttributeAssign;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.util.ProgressReporter;
import thebeast.util.QuietProgressReporter;

import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class FeatureCollector {

  private Model model;
  private GroundAtoms atoms;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private IntVariable counter = interpreter.createIntVariable();

  private HashMap<FactorFormula, RelationExpression>
          collectQueries = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<FactorFormula, AttributeAssign>
          updateIndices = new HashMap<FactorFormula, AttributeAssign>();

  private HashMap<FactorFormula, RelationVariable>
          tmp = new HashMap<FactorFormula, RelationVariable>();

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private StatementFactory factory = TheBeast.getInstance().getNodServer().statementFactory();

  private ProgressReporter progressReporter;
  private HashMap<FactorFormula, ExpressionBuilder>
          builders = new HashMap<FactorFormula, ExpressionBuilder>();

  /**
   * Creates a new FeatureCollector for a given model
   *
   * @param model the model to use.
   */
  public FeatureCollector(Model model) {
    configure(model);
    setProgressReporter(new QuietProgressReporter());
  }

  /**
   * Configures this collector to work for a certain model
   *
   * @param model the model to use.
   */
  public void configure(Model model) {
    this.model = model;
    atoms = model.getSignature().createGroundAtoms();
    Weights weights = model.getSignature().createWeights();
    QueryGenerator generator = new QueryGenerator(weights, atoms);
    for (FactorFormula factor : model.getLocalFactorFormulas())
      if (factor.isParametrized()) {
        builders.put(factor, new ExpressionBuilder(TheBeast.getInstance().getNodServer()));
        collectQueries.put(factor, generator.generateLocalCollectorQuery(factor, atoms, weights));
        updateIndices.put(factor, factory.createAttributeAssign("index", builder.expr(counter).intPostInc().getInt()));
      }

  }

  /**
   * Collects a set of weight function arguments which later learners
   * then can learn the weights for.
   *
   * @param corpus  the corpus to collect the features arguments from
   * @param weights the weights object we will write the collected arguments to
   */
  public void collect(Corpus corpus, Weights weights) {

    progressReporter.started();
    QueryGenerator generator = new QueryGenerator();
    int count = 0;
    for (GroundAtoms atoms : corpus){
      for (FactorFormula factor : builders.keySet()){
        ExpressionBuilder builder = builders.get(factor);
        builder.expr(generator.generateLocalCollectorQuery(factor, atoms, weights));
      }
      progressReporter.progressed();
      ++count;
    }
    progressReporter.finished();
    progressReporter.started();
    for (FactorFormula factor : builders.keySet()){
      ExpressionBuilder builder = builders.get(factor);
      builder.union(count);
      RelationVariable tmp = interpreter.createRelationVariable(factor.getWeightFunction().getHeading());
      interpreter.assign(tmp, builder.getRelation());
      interpreter.update(tmp, updateIndices.get(factor));
      interpreter.insert(weights.getRelation(factor.getWeightFunction()), tmp);
      progressReporter.progressed();
    }
    int newFeatures = counter.value().getInt() - weights.getFeatureCounter().value().getInt();
    interpreter.assign(weights.getFeatureCounter(), counter);
    interpreter.append(weights.getWeights(), newFeatures, 0.0);
    progressReporter.finished();
  }


  public ProgressReporter getProgressReporter() {
    return progressReporter;
  }

  /**
   * Sets this collector's progress reporter which will learn about each new instance processed.
   * @param progressReporter a progress reporter.
   */
  public void setProgressReporter(ProgressReporter progressReporter) {
    this.progressReporter = progressReporter;
  }
}
