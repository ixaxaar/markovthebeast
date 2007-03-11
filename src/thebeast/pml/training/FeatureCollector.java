package thebeast.pml.training;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.AttributeAssign;
import thebeast.nod.statement.Insert;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.type.Heading;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.IntVariable;
import thebeast.pml.*;
import thebeast.pml.corpora.Corpus;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.function.WeightFunction;
import thebeast.util.ProgressReporter;
import thebeast.util.QuietProgressReporter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Sebastian Riedel
 */
public class FeatureCollector implements HasProperties {

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
  private Weights weights;

  private HashSet<WeightFunction>
          collectAll = new HashSet<WeightFunction>();


  /**
   * Creates a new FeatureCollector for a given model
   *
   * @param model   the model to use.
   * @param weights the weights to fill up.
   */
  public FeatureCollector(Model model, Weights weights) {
    configure(model, weights);
    setProgressReporter(new QuietProgressReporter());
  }

  /**
   * Configures this collector to work for a certain model
   *
   * @param model   the model to use.
   * @param weights the weights to fill up.
   */
  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    atoms = model.getSignature().createGroundAtoms();
    QueryGenerator generator = new QueryGenerator(weights, atoms);
    StatementFactory statementFactory = TheBeast.getInstance().getNodServer().statementFactory();
    for (FactorFormula factor : model.getFactorFormulas())
      if (factor.isParametrized()) {
        RelationExpression query = generator.generateCollectorQuery(factor, atoms, weights);
        inserts.put(factor, statementFactory.createInsert(weights.getRelation(factor.getWeightFunction()), query));
        updateIndices.put(factor, factory.createAttributeAssign("index", builder.expr(counter).intPostInc().getInt()));
      }

  }

  public void collect(Corpus corpus) {
    collect(corpus.iterator());
  }

  /**
   * Collects a set of weight function arguments which later learners then can learn the weights for.
   *
   * @param corpus the corpus to collect the features arguments from
   */
  public void collect(Iterator<GroundAtoms> corpus) {

    HashSet<WeightFunction> done = new HashSet<WeightFunction>();

    progressReporter.started("Instantiating Features");
    for (FactorFormula factor : model.getFactorFormulas())
      if (factor.isParametrized()) {
        WeightFunction function = factor.getWeightFunction();
        if ( collectAll.contains(function)) {
          Heading heading = function.getHeading();
          for (Attribute attribute : heading.attributes()) {
            builder.allConstants((CategoricalType) attribute.type()).from(attribute.name());
          }
          for (Attribute attribute : heading.attributes()) {
            builder.id(attribute.name()).categoricalAttribute(attribute.name(), "value");
          }
          builder.id(function.getIndexAttribute().name()).num(0);
          builder.tuple(function.getArity() + 1).select();
          builder.query();
          interpreter.assign(weights.getRelation(function), builder.getRelation());
          progressReporter.progressed();
          done.add(function);
        }
      }
    progressReporter.finished();

    this.atoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
    progressReporter.started("Collecting Features");
    while (corpus.hasNext()) {
      this.atoms.load(corpus.next(), model.getInstancePredicates());
      for (FactorFormula factor : inserts.keySet()) {
        if (!done.contains(factor.getWeightFunction()))
          interpreter.interpret(inserts.get(factor));
      }
      progressReporter.progressed();
    }

    for (FactorFormula factor : updateIndices.keySet()) {
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
   *
   * @param progressReporter a progress reporter.
   */
  public void setProgressReporter(ProgressReporter progressReporter) {
    this.progressReporter = progressReporter;
  }

  /**
   * Calculates the memory (int bytes) this collector is occupying
   *
   * @return memory usage in bytes.
   */
  public int getUsedMemory() {
    return atoms.getMemoryUsage();
  }




  public void setCollectAll(WeightFunction function, boolean collectAll){
    if (collectAll) this.collectAll.add(function);
    else this.collectAll.remove(function);
  }

  public void setProperty(PropertyName name, Object value) {

    if (name.getHead().equals("all"))
      setCollectAll(model.getSignature().getWeightFunction(name.getTail().getHead()),(Boolean)value);
  }




  public Object getProperty(PropertyName name) {
    return null;
  }
}
