package thebeast.pml.training;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.AttributeAssign;
import thebeast.nod.statement.Insert;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.IntVariable;
import thebeast.pml.GroundAtoms;
import thebeast.pml.Model;
import thebeast.pml.TheBeast;
import thebeast.pml.Weights;
import thebeast.pml.corpora.Corpus;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
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

  private HashMap<FactorFormula, AttributeAssign>
          updateIndices = new HashMap<FactorFormula, AttributeAssign>();
  private HashMap<FactorFormula, Insert>
          inserts = new HashMap<FactorFormula, Insert>();

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private StatementFactory factory = TheBeast.getInstance().getNodServer().statementFactory();

  private ProgressReporter progressReporter;
  private static final int INSERT_INTERVAL = 1;
  private Weights weights;


  /**
   * Creates a new FeatureCollector for a given model
   *
   * @param model the model to use.
   * @param weights the weights to fill up.
   */
  public FeatureCollector(Model model, Weights weights) {
    configure(model, weights);
    setProgressReporter(new QuietProgressReporter());
  }

  /**
   * Configures this collector to work for a certain model
   *
   * @param model the model to use.
   * @param weights the weights to fill up.
   */
  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    atoms = model.getSignature().createGroundAtoms();
    QueryGenerator generator = new QueryGenerator(weights, atoms);
    StatementFactory statementFactory = TheBeast.getInstance().getNodServer().statementFactory();
    for (FactorFormula factor : model.getLocalFactorFormulas())
      if (factor.isParametrized()) {
        RelationExpression query = generator.generateLocalCollectorQuery(factor, atoms, weights);
        inserts.put(factor, statementFactory.createInsert(weights.getRelation(factor.getWeightFunction()),query));
        updateIndices.put(factor, factory.createAttributeAssign("index", builder.expr(counter).intPostInc().getInt()));

      }

  }

  /**
   * Collects a set of weight function arguments which later learners
   * then can learn the weights for.
   *
   * @param corpus  the corpus to collect the features arguments from
   */
  public void collect(Corpus corpus) {

    progressReporter.started();

    for (GroundAtoms atoms : corpus){
      this.atoms.load(atoms);
      for (FactorFormula factor : inserts.keySet()){
        interpreter.interpret(inserts.get(factor));
        //System.out.println("in collector:" + weights.getRelation(factor.getWeightFunction()).value());
      }
      progressReporter.progressed();
    }

    for (FactorFormula factor : updateIndices.keySet()){
      interpreter.update(weights.getRelation(factor.getWeightFunction()), updateIndices.get(factor));
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

  /**
   * Calculates the memory (int bytes) this collector is occupying
   * @return memory usage in bytes.
   */
  public int getUsedMemory(){
    return atoms.getUsedMemory();
  }

}
