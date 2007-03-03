package thebeast.pml.training;

import thebeast.pml.*;
import thebeast.pml.corpora.Corpus;
import thebeast.util.ProgressReporter;
import thebeast.util.QuietProgressReporter;
import thebeast.util.PrecisionRecallProgressReporter;
import thebeast.nod.variable.ArrayVariable;
import thebeast.nod.statement.Interpreter;

/**
 * @author Sebastian Riedel
 */
public class OnlineLearner implements Learner, HasProperties {

  private Solver solver = new CuttingPlaneSolver();
  private Learner collector = null;
  private GroundAtoms instance;
  private Solution solution;
  private Scores scores;
  private LocalFeatures features;
  private LocalFeatureExtractor extractor;
  private Evaluation evaluation;
  private UpdateRule updateRule;
  private SparseVector guess;
  private SparseVector gold;
  private GroundFormulas goldGroundFormulas;
  private Solution goldSolution;
  private Weights weights;
  private Model model;
  private PrecisionRecallProgressReporter progressReporter = new QuietProgressReporter();
  private boolean averaging = false;
  private ArrayVariable average;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private int count;

  private int numEpochs;

  public OnlineLearner(Model model, Weights weights) {
    configure(model, weights);
    setSolver(new CuttingPlaneSolver());
  }


  public ProgressReporter getProgressReporter() {
    return progressReporter;
  }

  public void setProgressReporter(PrecisionRecallProgressReporter progressReporter) {
    this.progressReporter = progressReporter;
  }

  public OnlineLearner(Model model, Weights weights, Solver solver) {
    configure(model, weights);
    setSolver(solver);
  }

  public void setNumEpochs(int numEpochs) {
    this.numEpochs = numEpochs;
  }


  public int getNumEpochs() {
    return numEpochs;
  }

  public Solver getSolver() {
    return solver;
  }

  public void setSolver(Solver solver) {
    this.solver = solver;
    solver.configure(model, weights);
  }

  public Learner getCollector() {
    return collector;
  }

  public void setCollector(Learner collector) {
    this.collector = collector;
  }


  public UpdateRule getUpdateRule() {
    return updateRule;
  }

  public void setUpdateRule(UpdateRule updateRule) {
    this.updateRule = updateRule;
  }


  public void learn(Corpus corpus) {
    setUpAverage();
    for (int epoch = 0; epoch < numEpochs; ++epoch) {
      progressReporter.started();
      for (GroundAtoms data : corpus) {
        learn(data);
      }
      updateRule.endEpoch();
      progressReporter.finished();
    }
    finalizeAverage();
  }

  private void finalizeAverage() {
    if (averaging && average != null) {
      interpreter.scale(average, 1.0 / count);
      interpreter.assign(weights.getWeights(), average);
    }
    average = null;
  }

  private void setUpAverage() {
    if (averaging) {
      average = interpreter.createDoubleArrayVariable(weights.getFeatureCount());
      count = 0;
    }
  }

  public void startEpoch() {
    progressReporter.started();
  }

  public void endEpoch() {
    updateRule.endEpoch();
    progressReporter.finished();
  }

  public void learn(GroundAtoms data) {
    //load the instance from the corpus into our local variable
    instance.load(data);

    //either load the feature vector or extract it
    extractor.extract(instance, features);
    //features.extract(instance);

    //System.out.println(features);

    //use the feature vector and weight to score ground atoms
    scores.score(features, this.weights);

    //System.out.println(features.toVerboseString());
    System.out.println(scores);
    //use the scores to solve the model
    solver.setObservation(instance);
    solver.setScores(scores);
    solver.solve();
    solution.getGroundAtoms().load(solver.getAtoms());

    //evaluate the guess vs the gold.
    evaluation.evaluate(instance, solution.getGroundAtoms());

    //extract features
    guess.load(solution.extract(features));

    goldGroundFormulas.extract(instance);
    goldSolution.load(instance, goldGroundFormulas);
    gold.load(goldSolution.extract());

    //update the weights
    updateRule.update(gold, guess, evaluation, this.weights);

    updateAverage();

    progressReporter.progressed(
            evaluation.getFalsePositivesCount(), evaluation.getFalseNegativesCount(),
            evaluation.getGoldCount(), evaluation.getGuessCount());
  }

  private void updateAverage() {
    if (averaging && average != null) {
      interpreter.add(average, weights.getWeights(), 1.0);
      ++count;
    }
  }

  public void configure(Model model, Weights weights) {
    this.weights = weights;
    this.model = model;
    instance = model.getSignature().createGroundAtoms();
    solution = new Solution(model, weights);
    scores = new Scores(model, weights);
    features = new LocalFeatures(model, weights);
    extractor = new LocalFeatureExtractor(model, weights);
    evaluation = new Evaluation(model);
    updateRule = new PerceptronUpdateRule();
    guess = new SparseVector();
    gold = new SparseVector();
    goldGroundFormulas = new GroundFormulas(model, weights);
    goldSolution = new Solution(model, weights);
    solver.configure(model, weights);
  }

  public void learn(TrainingInstances instances) {
    setUpAverage();
    for (int epoch = 0; epoch < numEpochs; ++epoch) {
      progressReporter.started();
      for (TrainingInstance instance : instances) {
        learn(instance);
      }
      updateRule.endEpoch();
      progressReporter.finished();
    }
    finalizeAverage();
  }

  private void learn(TrainingInstance data) {
    //load the instance from the corpus into our local variable
    instance.load(data.getData());

    //either load the feature vector or extract it
    features.load(data.getFeatures());

    //System.out.println(features);

    //use the feature vector and weight to score ground atoms
    scores.score(features, this.weights);

    //use the scores to solve the model
    solver.setObservation(data.getData());
    solver.setScores(scores);
    solver.solve();
    solution.getGroundAtoms().load(solver.getAtoms());
    //solver.solve(instance, scores, solution);
    //System.out.println(solution.getGroundAtoms());

    //evaluate the guess vs the gold.
    evaluation.evaluate(instance, solution.getGroundAtoms());

    //extract features (or load)
    guess.load(solution.extract(features));
    gold.load(data.getGold());

    //update the weights
    updateRule.update(gold, guess, evaluation, this.weights);

    progressReporter.progressed(
            evaluation.getFalsePositivesCount(), evaluation.getFalseNegativesCount(),
            evaluation.getGoldCount(), evaluation.getGuessCount());

  }


  public boolean isAveraging() {
    return averaging;
  }

  public void setAveraging(boolean averaging) {
    this.averaging = averaging;
  }

  public void setProperty(PropertyName name, Object value) {
    if ("solver".equals(name.getHead())) {
      solver.setProperty(name.getTail(), value);
    } else if ("numEpochs".equals(name.getHead())) {
      setNumEpochs((Integer) value);
    } else if ("update".equals(name.getHead())) {
      if ("mira".equals(value.toString()))
        setUpdateRule(new MiraUpdateRule());
      else if ("perceptron".equals(value.toString()))
        setUpdateRule(new PerceptronUpdateRule());
      else throw new IllegalPropertyValueException(name, value);
    } else if ("average".equals(name.getHead())) {
      if ("true".equals(value))
        setAveraging(true);
      else if ("false".equals(name.getHead()))
        setAveraging(false);
      else throw new IllegalPropertyValueException(name, value);
    }
  }

  public Object getProperty(PropertyName name) {
    if ("solution".equals(name.getHead()))
      return solution.getGroundAtoms();
    if ("gold".equals(name.getHead()))
      return gold;
    if ("guess".equals(name.getHead()))
      return guess;
    return null;
  }
}
