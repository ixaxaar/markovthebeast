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
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.*;
import thebeast.pml.corpora.Corpus;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.function.WeightFunction;
import thebeast.util.ProgressReporter;
import thebeast.util.QuietProgressReporter;
import thebeast.util.Counter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
          inserts = new LinkedHashMap<FactorFormula, Insert>();


  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private StatementFactory factory = TheBeast.getInstance().getNodServer().statementFactory();

  private ProgressReporter progressReporter;
  private Weights weights;

  private HashSet<WeightFunction>
          collectAll = new HashSet<WeightFunction>();

  private HashSet<WeightFunction>
          collectWithNeg = new HashSet<WeightFunction>();


  private int cutoff = 0;
  private HashMap<WeightFunction, Integer> cutoffs = new HashMap<WeightFunction, Integer>();

  private double initialWeight;


  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(String.format("%-20s: %-5d\n", "Cutoff", cutoff));
    return result.toString();
  }

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
    QueryGenerator generator = new QueryGenerator(model, weights, atoms);
    StatementFactory statementFactory = TheBeast.getInstance().getNodServer().statementFactory();
    inserts.clear();
    updateIndices.clear();
    for (FactorFormula factor : model.getFactorFormulas())
      if (factor.usesWeights()) {
        RelationExpression query = generator.generateCollectorQuery(factor, atoms,
                collectWithNeg.contains(factor.getWeightFunction()), weights);
        inserts.put(factor, statementFactory.createInsert(weights.getRelation(factor.getWeightFunction()), query));
        updateIndices.put(factor, factory.createAttributeAssign("index", builder.expr(counter).intPostInc().getInt()));
      }

  }


  public double getInitialWeight() {
    return initialWeight;
  }

  public void setInitialWeight(double initialWeight) {
    this.initialWeight = initialWeight;
  }

  /**
   * Collects all features from the corpus. This method creates a set of weight function arguments that can later be
   * trained to have non-zero weights.
   *
   * @param corpus a corpus conforming to the signature of the model this collector has been configured with.
   */
  public void collect(Corpus corpus) {
    collect(corpus.iterator());
  }

  /**
   * The collector can remove all features that haven't been seen often enough.
   *
   * @return the number of times a feature has to be in the corpus in order to be collected. This is 0 by default.
   */
  public int getCutoff() {
    return cutoff;
  }

  /**
   * The collector can remove all features that haven't been seen often enough.
   *
   * @param cutoff the number of times a feature has to be in the corpus in order to be collected. This is 0 by default.
   *               Note that this number does not affect weight functions for which we take all possible
   *               instantiations.
   */
  public void setCutoff(int cutoff) {
    this.cutoff = cutoff;
  }


  public void setCutoff(WeightFunction weightFunction, int cutoff) {
    cutoffs.put(weightFunction, cutoff);
  }

  public int getCutoff(WeightFunction weightFunction) {
    Integer result = cutoffs.get(weightFunction);
    return result == null ? cutoff : result;
  }

  /**
   * Collects a set of weight function arguments which later learners then can learn the weights for.
   *
   * @param corpus the corpus to collect the features arguments from
   */
  public void collect(Iterator<GroundAtoms> corpus) {

    HashSet<WeightFunction> done = new HashSet<WeightFunction>();

    this.atoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
    progressReporter.started("Collecting Features");
    while (corpus.hasNext()) {
      this.atoms.load(corpus.next(), model.getInstancePredicates());
      for (FactorFormula factor : inserts.keySet()) {
        if (factor.getWeightFunction().getArity() > 0 && !collectAll.contains(factor.getWeightFunction()))
          interpreter.interpret(inserts.get(factor));
      }
      progressReporter.progressed();
    }
    progressReporter.finished();

    progressReporter.started("Instantiating Features");
    for (FactorFormula factor : model.getFactorFormulas())
      if (factor.usesWeights()) {
        WeightFunction function = factor.getWeightFunction();
        if (function.getArity() == 0) {
          weights.addWeight(function, 0.0);
        } else if (collectAll.contains(function)) {
          Heading heading = function.getHeading();
          for (Attribute attribute : heading.attributes()) {
            builder.allConstants((CategoricalType) attribute.type()).from(attribute.name());
          }
          for (Attribute attribute : heading.attributes()) {
            builder.id(attribute.name()).categoricalAttribute(attribute.name(), "value");
          }
          builder.id(function.getIndexAttribute().name()).num(0);
          builder.id(function.getCountAttribute().name()).num(1);
          builder.tuple(function.getArity() + 2).select();
          builder.query();
          interpreter.assign(weights.getRelation(function), builder.getRelation());
          progressReporter.progressed();
          done.add(function);
        }
      }
    progressReporter.finished();

    //cutoff features
    progressReporter.started("Cutoff features");
    for (WeightFunction w : model.getLocalWeightFunctions()) {
      int cutoff = getCutoff(w);
      if (cutoff > 0 && !collectAll.contains(w) && w.getArity() > 0) {
        RelationVariable relation = weights.getRelation(w);
        RelationVariable tmp = interpreter.createRelationVariable(w.getIndexedHeading());
        builder.expr(relation).intAttribute("count").num(cutoff).intGreaterThan().restrict();
        interpreter.assign(tmp, builder.getRelation());
        interpreter.assign(relation, tmp);
        progressReporter.progressed();
      }
    }
    progressReporter.finished();

    for (FactorFormula factor : updateIndices.keySet()) {
      interpreter.update(weights.getRelation(factor.getWeightFunction()), updateIndices.get(factor));
    }
    int newFeatures = counter.value().getInt() - weights.getFeatureCounter().value().getInt();
    interpreter.assign(weights.getFeatureCounter(), counter);
    interpreter.append(weights.getWeights(), newFeatures, initialWeight);
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


  public void setCollectAll(WeightFunction function, boolean collectAll) {
    if (function == null) throw new IllegalArgumentException("function must not be null!");
    if (collectAll) this.collectAll.add(function);
    else this.collectAll.remove(function);
  }

  public void setCollectWithNeg(WeightFunction function, boolean collectAll) {
    if (function == null) throw new IllegalArgumentException("function must not be null!");
    if (collectAll) this.collectWithNeg.add(function);
    else this.collectWithNeg.remove(function);
  }


  public void setProperty(PropertyName name, Object value) {

    if (name.getHead().equals("all")) {
      WeightFunction weightFunction = model.getSignature().getWeightFunction(name.getTail().getHead());
      if (weightFunction == null) throw new NoSuchPropertyException(name);
      setCollectAll(weightFunction, (Boolean) value);
    } else if (name.getHead().equals("neg")) {
      WeightFunction weightFunction = model.getSignature().getWeightFunction(name.getTail().getHead());
      if (weightFunction == null) throw new NoSuchPropertyException(name);
      setCollectWithNeg(weightFunction, (Boolean) value);
    } else if (name.getHead().equals("init")) {
      setInitialWeight((Double) value);
    } else if (name.getHead().equals("cutoff")) {
      if (name.isTerminal())
        setCutoff((Integer) value);
      else {
        WeightFunction weightFunction = model.getSignature().getWeightFunction(name.getTail().getHead());
        setCutoff(weightFunction, (Integer) value);
      }
    }
  }


  public Object getProperty(PropertyName name) {
    return null;
  }
}
