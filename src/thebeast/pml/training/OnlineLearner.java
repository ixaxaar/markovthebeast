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
  private PerformanceProgressReporter progressReporter = new QuietProgressReporter();
  private boolean averaging = false;
  private ArrayVariable average;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private int count;
  private Profiler profiler = new NullProfiler();
  private int maxCandidates = 1000;
  private LossFunction lossFunction;
  private boolean penalizeGold = false;
  private boolean useGreedy = true;
  private boolean saveAfterEpoch = true;
  private boolean initializeWeights = false;
  private double initialWeight = 0;
  private String savePrefix = "/tmp/epoch_";
  private Stack<FeatureVector> allVectors = new Stack<FeatureVector>();
  private Stack<FeatureVector> usableVectors = new Stack<FeatureVector>();

  private int numEpochs;
  private int maxViolations = 1;//Integer.MAX_VALUE;
  private int instanceNr;

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


  private void finalizeAverage(Weights weights) {
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

  public void learn(TrainingInstances instances) {
    profiler.start("learn");
    setUpAverage();
    if (initializeWeights)
      weights.setAllWeights(initialWeight);
    for (int epoch = 0; epoch < numEpochs; ++epoch) {
      profiler.start("epoch");
      progressReporter.started("Epoch " + epoch);
      instanceNr = 0;
      for (TrainingInstance instance : instances) {
        learn(instance);
//        if (instanceNr >= 550 && instanceNr <= 560)
//          ((CuttingPlaneSolver)solver).printHistory(System.out);
        ++instanceNr;
//        if (instanceNr % 100 == 99)
//          System.out.println(instances.getUsedMemory());
        //System.out.println(TheBeast.getInstance().getNodServer().interpreter().getMemoryString());
        //((ILPSolverLpSolve)((IntegerLinearProgram)((CuttingPlaneSolver)solver).getPropositionalModel()).getSolver()).delete();
//        solver = new CuttingPlaneSolver(new IntegerLinearProgram(model,weights, new ILPSolverLpSolve()));
//        solver.configure(model,weights);

      }
      updateRule.endEpoch();
      progressReporter.finished();
      profiler.end();
      //System.out.println(TheBeast.getInstance().getNodServer().interpreter().getMemoryString());
      if (saveAfterEpoch) saveCurrentWeights(epoch);
    }
    finalizeAverage(weights);
    profiler.end();
  }

  private void saveCurrentWeights(int epoch) {
    try {
      File file = new File(savePrefix + epoch + ".dmp");
      file.delete();
      FileSink sink = TheBeast.getInstance().getNodServer().createSink(file, 1024);
      if (averaging) {
        Weights copy = weights.copy();
        finalizeAverage(weights);
        copy.write(sink);
      } else {
        weights.write(sink);
      }
      sink.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void learn(TrainingInstance data) {
    //load the instance from the corpus into our local variable
    profiler.start("learn one");
    goldAtoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
    goldAtoms.load(data.getData(), model.getInstancePredicates());

    //either load the feature vector or extract it
    features.load(data.getFeatures());

    //use the feature vector and weight to score ground atoms
    profiler.start("score");
    //scores.score(features, this.weights);
    scores.scoreWithGroups(features, data.getData());
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
    double loss = lossFunction.loss(goldAtoms, solver.getBestAtoms());
    //System.out.println(loss);
    profiler.end();

    gold.load(data.getGold());

    //extract features (or load)
    profiler.start("extract");
    List<GroundAtoms> candidateAtoms = solver.getCandidateAtoms();
    List<GroundFormulas> candidateFormulas = solver.getCandidateFormulas();
    List<FeatureVector> candidates = new ArrayList<FeatureVector>(candidateAtoms.size());
    List<Double> losses = new ArrayList<Double>(candidateAtoms.size());

    //new SentencePrinter().print(goldAtoms, System.out);
    //System.out.println("Gold:" + weights.toString(gold));
    for (int i = 0; i < candidateAtoms.size() && i < maxCandidates; ++i) {
      int violationCount = candidateFormulas.get(i).getViolationCount();
      //maxViolations = 1;
      if (violationCount < maxViolations) {
        //System.out.print(violationCount + " ");
        GroundAtoms guessAtoms = candidateAtoms.get(i);
//        if (instanceNr == 0){
//          new SentencePrinter().print(guessAtoms, System.out);
//        }
        solution.getGroundAtoms().load((guessAtoms));
        solution.getGroundFormulas().load(candidateFormulas.get(i));
        FeatureVector features;
        if (usableVectors.isEmpty()){
          features = new FeatureVector();
          allVectors.push(features);
        } else {
          features = usableVectors.pop();
        }
        solution.extractInPlace(this.features,features);
//        FeatureVector features = solution.extract(this.features);
        candidates.add(features);
        losses.add(lossFunction.loss(goldAtoms, guessAtoms));
        //new SentencePrinter().print(guessAtoms, System.out);
        //System.out.println("Guess " + i + ": " + weights.toString(features));
      }
    }

    if (useGreedy && solver.getGreedyFormulas().getViolationCount() <= maxViolations
            && candidates.size() < maxCandidates && solver.doesOwnLocalSearch()) {
      solution.getGroundAtoms().load(solver.getGreedyAtoms());
      solution.getGroundFormulas().load(solver.getGreedyFormulas());
      FeatureVector features;
      if (usableVectors.isEmpty()){
        features = new FeatureVector();
        allVectors.push(features);
      } else {
        features = usableVectors.pop();
      }
      solution.extractInPlace(this.features,features);
      //FeatureVector features = solution.extract(this.features);
      //System.out.println(features.getLocal());
      //features.getNonnegative().load(gold.getNonnegative());
      //features.getNonpositive().load(gold.getNonpositive());
      candidates.add(features);
      losses.add(lossFunction.loss(goldAtoms, solver.getGreedyAtoms()));
    }
    //System.out.println(losses);

    //guess.load(solution.extract(features));
    profiler.end();

    //update the weights
    profiler.start("update");
    if (candidates.size() > 0) updateRule.update(gold, candidates, losses, this.weights);
    profiler.end();

    //System.out.println(losses);
    updateAverage();

    progressReporter.progressed(loss, solver.getIterationCount());

    //add feature vectors for reuse
    for (FeatureVector vector: allVectors){
      vector.clear();
      usableVectors.add(vector);
    }

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


  public int getMaxViolations() {
    return maxViolations;
  }

  public void setMaxViolations(int maxViolations) {
    this.maxViolations = maxViolations;
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
    } else if ("maxViolations".equals(name.getHead())) {
      setMaxViolations((Integer) value);
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
        updateRule.setProperty(name.getTail(), value);
    } else if ("average".equals(name.getHead())) {
      setAveraging((Boolean) value);
    } else if ("penalizeGold".equals(name.getHead())) {
      setPenalizeGold((Boolean) value);
    } else if ("useGreedy".equals(name.getHead())) {
      setUseGreedy((Boolean) value);
    } else if ("initWeights".equals(name.getHead())) {
      setInitializeWeights((Boolean) value);
    } else if ("initWeight".equals(name.getHead())) {
      setInitialWeight((Double) value);
    } else if ("loss".equals(name.getHead())) {
      if (value.equals("avgF1"))
        setLossFunction(new AverageF1Loss(model));
      else if (value.equals("avgNumErrors"))
        setLossFunction(new AverageNumErrors(model));
      else if (value.equals("globalNumErrors"))
        setLossFunction(new GlobalNumErrors(model));
      else if (value.equals("globalF1"))
        setLossFunction(new GlobalF1Loss(model));
      else
        throw new IllegalPropertyValueException(name, value);
    } else if (name.getHead().equals("profile"))
      setProfiler(((Boolean) value) ? new TreeProfiler() : new NullProfiler());

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

  public void setInitializeWeights(boolean initializeWeights) {
    this.initializeWeights = initializeWeights;
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
