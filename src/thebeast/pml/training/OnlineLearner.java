package thebeast.pml.training;

import thebeast.nod.statement.Interpreter;
import thebeast.nod.variable.ArrayVariable;
import thebeast.pml.*;
import thebeast.pml.corpora.Corpus;
import thebeast.pml.solve.CuttingPlaneSolver;
import thebeast.pml.solve.LocalSolver;
import thebeast.pml.solve.Solver;
import thebeast.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class OnlineLearner implements Learner, HasProperties {

  private Solver solver = new CuttingPlaneSolver();
  private Learner collector = null;
  private GroundAtoms goldAtoms;
  private Solution solution;
  private Scores scores;
  private LocalFeatures features;
  private LocalFeatureExtractor extractor;
  private Evaluation evaluation;
  private UpdateRule updateRule;
  private FeatureVector guess;
  private FeatureVector gold;
  private GroundFormulas goldGroundFormulas;
  private Solution goldSolution;
  private Weights weights;
  private Model model;
  private PerformanceProgressReporter progressReporter = new QuietProgressReporter();
  private boolean averaging = false;
  private ArrayVariable average;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private int count;
  private Profiler profiler = new NullProfiler();
  private int maxCandidates = 1000;
  private LossFunction lossFunction;
  private boolean penalizeGold = false;

  private int numEpochs;

  public OnlineLearner(Model model, Weights weights) {
    configure(model, weights);
    setSolver(new CuttingPlaneSolver());
  }


  public ProgressReporter getProgressReporter() {
    return progressReporter;
  }

  public void setProgressReporter(PerformanceProgressReporter progressReporter) {
    this.progressReporter = progressReporter;
  }


  public int getMaxCandidates() {
    return maxCandidates;
  }

  public void setMaxCandidates(int maxCandidates) {
    this.maxCandidates = maxCandidates;
  }

  public OnlineLearner(Model model, Weights weights, Solver solver) {
    configure(model, weights);
    setSolver(solver);
  }


  public LossFunction getLossFunction() {
    return lossFunction;
  }

  public void setLossFunction(LossFunction lossFunction) {
    this.lossFunction = lossFunction;
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


  public Profiler getProfiler() {
    return profiler;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
    solver.setProfiler(profiler);
    if (solution != null) solution.setProfiler(profiler);
  }

  public void learn(GroundAtoms data) {
    //load the instance from the corpus into our local variable
    goldAtoms.load(data);

    //either load the feature vector or extract it
    extractor.extract(goldAtoms, features);
    //features.extract(instance);

    //System.out.println(features);

    //use the feature vector and weight to score ground atoms
    scores.score(features, this.weights);

    //System.out.println(features.toVerboseString());
    System.out.println(scores);
    //use the scores to solve the model
    solver.setObservation(goldAtoms);
    solver.setScores(scores);
    solver.solve();
    solution.getGroundAtoms().load(solver.getBestAtoms());

    //evaluate the guess vs the gold.
    evaluation.evaluate(goldAtoms, solution.getGroundAtoms());

    //extract features
    guess.load(solution.extract(features));

    goldGroundFormulas.extract(goldAtoms);
    goldSolution.load(goldAtoms, goldGroundFormulas);
    gold.load(goldSolution.extract());

    //update the weights
    //updateRule.update(gold, c, evaluation, this.weights);

    updateAverage();

    progressReporter.progressed(
            0.0, 0);
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
    goldAtoms = model.getSignature().createGroundAtoms();
    solution = new Solution(model, weights);
    solution.setProfiler(profiler);
    scores = new Scores(model, weights);
    features = new LocalFeatures(model, weights);
    extractor = new LocalFeatureExtractor(model, weights);
    evaluation = new Evaluation(model);
    updateRule = new PerceptronUpdateRule();
    guess = new FeatureVector();
    gold = new FeatureVector();
    goldGroundFormulas = new GroundFormulas(model, weights);
    goldSolution = new Solution(model, weights);
    solver.configure(model, weights);
    lossFunction = new GlobalF1Loss(model);
  }

  public void learn(TrainingInstances instances) {
    profiler.start("learn");
    setUpAverage();
    for (int epoch = 0; epoch < numEpochs; ++epoch) {
      profiler.start("epoch");
      progressReporter.started();
      for (TrainingInstance instance : instances) {
        learn(instance);
      }
      updateRule.endEpoch();
      progressReporter.finished();
      profiler.end();
    }
    finalizeAverage();
    profiler.end();
  }

  private void learn(TrainingInstance data) {
    //load the instance from the corpus into our local variable
    profiler.start("learn one");
    goldAtoms.load(model.getGlobalAtoms(),model.getGlobalPredicates());
    goldAtoms.load(data.getData(),model.getInstancePredicates());

    //either load the feature vector or extract it
    features.load(data.getFeatures());

    //use the feature vector and weight to score ground atoms
    profiler.start("score");
    //scores.score(features, this.weights);
    scores.scoreWithGroups(features);
    if (penalizeGold)
      scores.penalize(goldAtoms);
    profiler.end();

    //use the scores to solve the model
    solver.setObservation(data.getData());
    solver.setScores(scores);
    solver.solve();
    solution.getGroundAtoms().load(solver.getBestAtoms(), model.getHiddenPredicates());
    solution.getGroundFormulas().load(solver.getBestFormulas());

    //evaluate the guess vs the gold.
    profiler.start("evaluate");
    evaluation.evaluate(goldAtoms, solver.getBestAtoms());
    profiler.end();

    gold.load(data.getGold());

    //extract features (or load)
    profiler.start("extract");
    List<GroundAtoms> candidateAtoms = solver.getCandidateAtoms();                                
    List<GroundFormulas> candidateFormulas = solver.getCandidateFormulas();
    List<FeatureVector> candidates = new ArrayList<FeatureVector>(candidateAtoms.size());
    List<Double> losses = new ArrayList<Double>(candidateAtoms.size());
    for (int i = 0; i < candidateAtoms.size() && i < maxCandidates; ++i) {
      GroundAtoms guessAtoms = candidateAtoms.get(i);
      solution.getGroundAtoms().load((guessAtoms));
      solution.getGroundFormulas().load(candidateFormulas.get(i));
      FeatureVector features = solution.extract(this.features);
      if (solution.getGroundFormulas().isDeterministic())
        features.loadSigned(gold);
      candidates.add(features);
      losses.add(lossFunction.loss(goldAtoms, guessAtoms));
    }

    //guess.load(solution.extract(features));
    profiler.end();

    //update the weights
    profiler.start("update");
    updateRule.update(gold, candidates, losses, this.weights);
    profiler.end();

    updateAverage();

    progressReporter.progressed(losses.get(0), losses.size());

//    for (UserPredicate pred : model.getHiddenPredicates()) {
//      System.out.println(goldAtoms.getGroundAtomsOf(pred));
//      System.out.println(candidateAtoms.get(0).getGroundAtomsOf(pred));
//    }
    //System.out.println(losses);

    profiler.end();
  }


  public boolean isAveraging() {
    return averaging;
  }

  public void setAveraging(boolean averaging) {
    this.averaging = averaging;
  }


  public boolean isPenalizeGold() {
    return penalizeGold;
  }

  public void setPenalizeGold(boolean penalizeGold) {
    this.penalizeGold = penalizeGold;
  }

  public void setProperty(PropertyName name, Object value) {
    if ("solver".equals(name.getHead())) {
      if (!name.isTerminal())
        solver.setProperty(name.getTail(), value);
      else {
        if ("local".equals(value))
          solver = new LocalSolver();
        else if ("cut".equals(value))
          solver = new CuttingPlaneSolver();
        else
          throw new IllegalPropertyValueException(name, value);
        solver.configure(model, weights);
      }

    } else if ("maxCandidates".equals(name.getHead())) {
      setMaxCandidates((Integer) value);
    } else if ("numEpochs".equals(name.getHead())) {
      setNumEpochs((Integer) value);
    } else if ("update".equals(name.getHead())) {
      if (name.isTerminal()) {
        if ("mira".equals(value.toString()))
          setUpdateRule(new MiraUpdateRule());
        else if ("perceptron".equals(value.toString()))
          setUpdateRule(new PerceptronUpdateRule());
        else throw new IllegalPropertyValueException(name, value);
      } else
        updateRule.setProperty(name.getTail(),value);
    } else if ("average".equals(name.getHead())) {
      setAveraging((Boolean) value);
    } else if ("penalize".equals(name.getHead())) {
      setPenalizeGold((Boolean) value);
    } else if ("loss".equals(name.getHead())) {
      if (value.equals("avgF1"))
        setLossFunction(new AverageF1Loss(model));
      else if (value.equals("avgNumErrors"))
        setLossFunction(new AverageNumErrors(model));
      else if (value.equals("globalF1"))
        setLossFunction(new GlobalF1Loss(model));
      else
        throw new IllegalPropertyValueException(name, value);
    } else if (name.getHead().equals("profile"))
      setProfiler(((Boolean) value) ? new TreeProfiler() : new NullProfiler());

  }

  public Object getProperty(PropertyName name) {
    if ("solution".equals(name.getHead()))
      return solution.getGroundAtoms();
    if ("profiler".equals(name.getHead()))
      return profiler;
    if ("solver".equals(name.getHead()))
      if (name.isTerminal())
        return solver;
      else
        return solver.getProperty(name.getTail());
    if ("gold".equals(name.getHead()))
      return gold;
    if ("guess".equals(name.getHead()))
      return guess;
    return null;
  }
}
