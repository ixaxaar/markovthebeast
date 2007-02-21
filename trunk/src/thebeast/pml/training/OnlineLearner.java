package thebeast.pml.training;

import thebeast.pml.*;
import thebeast.pml.corpora.Corpus;

/**
 * @author Sebastian Riedel
 */
public class OnlineLearner implements Learner {

  private Solver solver = new CuttingPlaneSolver();
  private Learner collector = null;
  private GroundAtoms instance;
  private Solution solution;
  private Scores scores;
  private LocalFeatures features;
  private Evaluation evaluation;
  private UpdateRule updateRule;
  private SparseVector guess;
  private SparseVector gold;
  private GroundFormulas goldGroundFormulas;
  private Solution goldSolution;
  private Weights weights;
  private Model model;

  private int numEpochs;

  private boolean initialized = false;

  public OnlineLearner(Model model, Weights weights) {
    configure(model, weights);
    setSolver(new CuttingPlaneSolver());
  }

  
  public void setNumEpochs(int numEpochs) {
    this.numEpochs = numEpochs;
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
    learn(corpus, new TrainingInstances());
  }

  public void learn(Corpus corpus, TrainingInstances instances) {
 
    //do one run to create training instances
    for (GroundAtoms data : corpus) {
      learnOne(data, instances);
    }

    updateRule.endEpoch();

    initialized = true;
    learn(instances);
    initialized = false;

  }

  public void learnOne(GroundAtoms data, TrainingInstances instances) {
    //load the instance from the corpus into our local variable
    instance.load(data);

    //either load the feature vector or extract it
    features.extract(instance);

    System.out.println(features);

    //use the feature vector and weight to score ground atoms
    scores.score(features, this.weights);

    //use the scores to solve the model
    solver.setObservation(instance);
    solver.setScores(scores);
    solver.solve();
    solution.getGroundAtoms().load(solver.getAtoms());
    //solver.solve(instance, scores, solution);

    //evaluate the guess vs the gold.
    evaluation.evaluate(instance, solution.getGroundAtoms());

    //extract features
    guess.load(solution.extract(features));

    goldGroundFormulas.extract(instance);
    goldSolution.load(instance, goldGroundFormulas);
    gold.load(goldSolution.extract());

    //update the weights
    updateRule.update(gold, guess, evaluation, this.weights);

    instances.add(data.copy(), features.copy(), gold.copy());
  }

  public void configure(Model model, Weights weights) {
    this.weights = weights;
    this.model = model;
    instance = model.getSignature().createGroundAtoms();
    solution = new Solution(model, weights);
    scores = new Scores(model, weights);
    features = new LocalFeatures(model, weights);
    evaluation = new Evaluation(model);
    updateRule = new PerceptronUpdateRule();
    guess = new SparseVector();
    gold = new SparseVector();
    goldGroundFormulas = new GroundFormulas(model, weights);
    goldSolution = new Solution(model, weights);
    solver.configure(model, weights);
  }

  public void learn(TrainingInstances instances) {

    for (int epoch = initialized ? 1 : 0; epoch < numEpochs; ++epoch) {

      for (TrainingInstance data : instances) {
        learnOne(data);
      }

      updateRule.endEpoch();
    }

  }

  private void learnOne(TrainingInstance data) {
    //load the instance from the corpus into our local variable
    instance.load(data.getData());

    //either load the feature vector or extract it
    features.load(data.getFeatures());

    //use the feature vector and weight to score ground atoms
    scores.score(features, this.weights);

    //use the scores to solve the model
    solver.setObservation(data.getData());
    solver.setScores(scores);
    solver.solve();
    solution.getGroundAtoms().load(solver.getAtoms());
    //solver.solve(instance, scores, solution);

    //evaluate the guess vs the gold.
    evaluation.evaluate(instance, solution.getGroundAtoms());

    //extract features (or load)
    guess.load(solution.extract(features));
    gold.load(data.getGold());

    //update the weights
    updateRule.update(gold, guess, evaluation, this.weights);

  }


}
