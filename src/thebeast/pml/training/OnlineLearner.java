package thebeast.pml.training;

import thebeast.nod.FileSink;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.variable.ArrayVariable;
import thebeast.pml.*;
import thebeast.pml.solve.CuttingPlaneSolver;
import thebeast.pml.solve.LocalSolver;
import thebeast.pml.solve.Solver;
import thebeast.util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
  private ProgressReporter progressReporter = new QuietProgressReporter();
  private boolean averaging = true;
  private ArrayVariable average;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private int count;
  private Profiler profiler = new NullProfiler();
  private LossFunction lossFunction;
  private boolean penalizeGold = false;
  private boolean rewardBad = false;
  private boolean maxLossScaling = false;
  private boolean useGreedy = true;
  private boolean saveAfterEpoch = true;
  private boolean initializeWeights = false;
  private double initialWeight = 0;
  private String savePrefix = "/tmp/epoch_";
  private Stack<FeatureVector> allVectors = new Stack<FeatureVector>();
  private Stack<FeatureVector> usableVectors = new Stack<FeatureVector>();

  private int numEpochs;
  private int minOrder = 1;
  private int minCandidates = 1;
  private int maxCandidates = 1;
  private int maxAtomCount = Integer.MAX_VALUE;

  public OnlineLearner(Model model, Weights weights) {
    configure(model, weights);
    setSolver(new CuttingPlaneSolver());
  }


  /**
   * Returns a string with properties of this learner;
   *
   * @return string with properties
   */
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(String.format("%-20s: %-5b\n", "averaging", averaging));
    result.append(String.format("%-20s: %-5b\n", "penalizeGold", penalizeGold));
    result.append(String.format("%-20s: %-5b\n", "rewardBad", rewardBad));
    result.append(String.format("%-20s: %-5d\n", "MinOrder", minOrder));
    result.append(String.format("%-20s: %-5d\n", "MaxCandidates", maxCandidates));
    result.append(String.format("%-20s: %-20s\n", "Loss", lossFunction.getClass().getName()));
    result.append("Solver:\n");
    result.append(solver);
    return result.toString();
  }

  public ProgressReporter getProgressReporter() {
    return progressReporter;
  }

  public void setProgressReporter(ProgressReporter progressReporter) {
    this.progressReporter = progressReporter;
  }


  /**
   * The minimum order is the minimum order a candidate has to have in order to be included in the update operation.
   *
   * @return the minimum order of a candidate to be included in learning.
   */
  public int getMinOrder() {
    return minOrder;
  }

  public void setMinOrder(int minOrder) {
    this.minOrder = minOrder;
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


  private void finalizeAverage(Weights weights) {
    if (averaging) {
      //if (averaging && average != null) {
      //interpreter.scale(average, 1.0 / count);
      interpreter.assign(weights.getLastWeights(), weights.getWeights());
      interpreter.assign(weights.getWeights(), average);
      interpreter.scale(weights.getWeights(), 1.0 / count);
      weights.setSeenInstances(count);
    }
    //average = null;
  }

  private void setUpAverage() {
    if (averaging) {
      count = weights.getSeenInstances();
      if (count == 0)
        average = interpreter.createDoubleArrayVariable(weights.getFeatureCount());
      else {
        average = interpreter.createArrayVariable(weights.getWeights());
        interpreter.scale(average, count);
        interpreter.assign(weights.getWeights(), weights.getLastWeights());
      }
    }
  }

  public void startEpoch() {
    progressReporter.started();
  }

  public void endEpoch() {
    updateRule.endEpoch();
    progressReporter.finished();
  }


  public boolean isUseGreedy() {
    return useGreedy;
  }

  public void setUseGreedy(boolean useGreedy) {
    this.useGreedy = useGreedy;
  }

  public Profiler getProfiler() {
    return profiler;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
    solver.setProfiler(profiler);
    if (solution != null) solution.setProfiler(profiler);
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
    updateRule = new MiraUpdateRule();
    guess = new FeatureVector();
    gold = new FeatureVector();
    goldGroundFormulas = new GroundFormulas(model, weights);
    goldSolution = new Solution(model, weights);
    solver.configure(model, weights);
    lossFunction = new GlobalNumErrors(model);
  }

  /**
   * Learns weights (the one we set using the configure method) using the specified set of training instances
   *
   * @param instances the training instances to use.
   */
  public void learn(TrainingInstances instances) {
    profiler.start("learn");
    progressReporter.setColumns("Loss", "F1", "Iterations", "Candidates");
    setUpAverage();
    if (initializeWeights)
      weights.setAllWeights(initialWeight);
    for (int epoch = 0; epoch < numEpochs; ++epoch) {
      profiler.start("epoch");
      progressReporter.started("Epoch " + epoch);
      scores.setPenalizeGoldScale(maxLossScaling ? epoch / (numEpochs - 1.0) : 1.0);
      scores.setRewardBadScale(maxLossScaling ? epoch / (numEpochs - 1.0) : 1.0);
      for (TrainingInstance instance : instances) {
        if (instance.getData().getGroundAtomCount() <= maxAtomCount) learn(instance);
      }
      updateRule.endEpoch();
      progressReporter.finished();
      profiler.end();
      if (saveAfterEpoch) saveCurrentWeights(epoch);
    }
    finalizeAverage(weights);
    profiler.end();
  }

  private void saveCurrentWeights(int epoch) {
    try {
      //write plain weights
      File file = new File(savePrefix + epoch + ".dmp");
      file.delete();
      FileSink sink = TheBeast.getInstance().getNodServer().createSink(file, 1024);
      weights.write(sink);
      sink.flush();

      //write averaged weights
      File avgFile = new File(savePrefix + epoch + ".avg.dmp");
      avgFile.delete();
      FileSink avgSink = TheBeast.getInstance().getNodServer().createSink(avgFile, 1024);
      Weights copy = weights.copy();
      finalizeAverage(copy);
      copy.write(avgSink);
      avgSink.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Process one training instance
   *
   * @param data the instance to process
   */
  private void learn(TrainingInstance data) {
    //load the instance from the corpus into our local variable
    profiler.start("learn one");
    goldAtoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
    goldAtoms.load(data.getData(), model.getInstancePredicates());

    //either load the feature vector or extract it
    if (data.getFeatures() == null)
      extractor.extract(data.getData(), features);
    else
      features.load(data.getFeatures());

    //use the feature vector and weight to score ground atoms
    profiler.start("score");
    if (data.getFeatures() == null)
      scores.score(features, data.getData());
    else
      scores.scoreWithGroups(features, data.getData());
    if (penalizeGold)
      scores.penalizeGold(goldAtoms);
    if (rewardBad)
      scores.rewardBad(goldAtoms);
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
    double loss = lossFunction.loss(goldAtoms, solver.getBestAtoms());
    //System.out.println(loss);
    profiler.end();

    gold.load(data.getGold());

    //extract features (or load)
    profiler.start("extract");
    List<FeatureVector> candidates = new ArrayList<FeatureVector>(solver.getCandidateCount());
    List<Double> losses = new ArrayList<Double>(solver.getCandidateCount());

    //new SentencePrinter().print(goldAtoms, System.out);
    //System.out.println("Gold:" + weights.toString(gold));
    for (int i = 0; i < solver.getCandidateCount() && losses.size() < maxCandidates; ++i) {
      int order = solver.getCandidateOrder(i);
      //maxViolations = 1;
      if (i < minCandidates || (order >= minOrder)) {
        //System.out.print(violationCount + " ");
        GroundAtoms guessAtoms = solver.getCandidateAtoms(i);
//        if (instanceNr == 0){
//          new SentencePrinter().print(guessAtoms, System.out);
//        }
        solution.getGroundAtoms().load((guessAtoms));
        profiler.start("get-formulas");
        solution.getGroundFormulas().load(solver.getCandidateFormulas(i));
        profiler.end();
        FeatureVector features;
        if (usableVectors.isEmpty()) {
          features = new FeatureVector();
          allVectors.push(features);
        } else {
          features = usableVectors.pop();
        }
        solution.extractInPlace(this.features, features);
//        FeatureVector features = solution.extract(this.features);
        candidates.add(features);
        losses.add(lossFunction.loss(goldAtoms, guessAtoms));
        //new SentencePrinter().print(guessAtoms, System.out);
        //System.out.println("Guess " + i + ": " + weights.toString(features));
      }
    }
    profiler.end();

    //update the weights
    profiler.start("update");
    if (candidates.size() > 0) updateRule.update(gold, candidates, losses, this.weights);
    profiler.end();

    //System.out.println(losses);
    updateAverage();

    progressReporter.progressed(loss, evaluation.getF1(), solver.getIterationCount(), losses.size());

    //add feature vectors for reuse
    for (FeatureVector vector : allVectors) {
      vector.clear();
      usableVectors.add(vector);
    }


    profiler.end();
  }


  public boolean isSaveAfterEpoch() {
    return saveAfterEpoch;
  }

  public void setSaveAfterEpoch(boolean saveAfterEpoch) {
    this.saveAfterEpoch = saveAfterEpoch;
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


  public boolean isMaxLossScaling() {
    return maxLossScaling;
  }

  public void setMaxLossScaling(boolean maxLossScaling) {
    this.maxLossScaling = maxLossScaling;
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
    } else if ("minCandidates".equals(name.getHead())) {
      setMinCandidates((Integer) value);
    } else if ("minOrder".equals(name.getHead())) {
      setMinOrder((Integer) value);
    } else if ("numEpochs".equals(name.getHead())) {
      setNumEpochs((Integer) value);
    } else if ("maxAtomCount".equals(name.getHead())) {
      setMaxAtomCount((Integer) value);
    } else if ("update".equals(name.getHead())) {
      if (name.isTerminal()) {
        if ("mira".equals(value.toString()))
          setUpdateRule(new MiraUpdateRule());
        else if ("perceptron".equals(value.toString()))
          setUpdateRule(new PerceptronUpdateRule());
        else if ("pa".equals(value.toString()))
          setUpdateRule(new PassiveAggressiveUpdateRule());
        else throw new IllegalPropertyValueException(name, value);
      } else
        updateRule.setProperty(name.getTail(), value);
    } else if ("average".equals(name.getHead())) {
      setAveraging((Boolean) value);
    } else if ("penalizeGold".equals(name.getHead())) {
      setPenalizeGold((Boolean) value);
    } else if ("rewardBad".equals(name.getHead())) {
      setRewardBad((Boolean) value);
    } else if ("maxLossScaling".equals(name.getHead())) {
      setMaxLossScaling((Boolean) value);
    } else if ("saveAfterEpoch".equals(name.getHead())) {
      setSaveAfterEpoch((Boolean)value);
    } else if ("initWeights".equals(name.getHead())) {
      setInitializeWeights((Boolean) value);
    } else if ("initWeight".equals(name.getHead())) {
      setInitialWeight((Double) value);
    } else if ("loss".equals(name.getHead())) {
      if (name.isTerminal()) {
        if (value.equals("avgF1"))
          setLossFunction(new AverageF1Loss(model));
        else if (value.equals("avgNumErrors"))
          setLossFunction(new AverageNumErrors(model));
        else if (value.equals("globalNumErrors"))
          setLossFunction(new GlobalNumErrors(model));
        else if (value.equals("fpnp"))
          setLossFunction(new GlobalNumErrors(model));
        else if (value.equals("falseNegatives"))
          setLossFunction(new GlobalNumErrors(model));
        else if (value.equals("globalF1"))
          setLossFunction(new GlobalF1Loss(model));
        else if (value.equals("exact"))
          setLossFunction(new ExactMatchLoss(model));
        else
          throw new IllegalPropertyValueException(name, value);
      } else
        lossFunction.setProperty(name.getTail(), value);
    } else if (name.getHead().equals("profile"))
      setProfiler(((Boolean) value) ? new TreeProfiler() : new NullProfiler());

  }

  private void setRewardBad(Boolean rewardBad) {
    this.rewardBad = rewardBad;
  }


  public double getInitialWeight() {
    return initialWeight;
  }

  public void setInitialWeight(double initialWeight) {
    this.initialWeight = initialWeight;
  }

  public boolean isInitializeWeights() {
    return initializeWeights;
  }


  public int getMinCandidates() {
    return minCandidates;
  }

  public void setMinCandidates(int minCandidates) {
    this.minCandidates = minCandidates;
  }

  public void setInitializeWeights(boolean initializeWeights) {
    this.initializeWeights = initializeWeights;
  }


  public int getMaxAtomCount() {
    return maxAtomCount;
  }

  public void setMaxAtomCount(int maxAtomCount) {
    this.maxAtomCount = maxAtomCount;
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
