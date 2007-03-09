package thebeast.pml.solve;

import thebeast.util.Profiler;
import thebeast.util.NullProfiler;
import thebeast.pml.*;

import java.util.List;
import java.util.Collections;

/**
 * The local solver only uses local information to find a solution. Most likely, this solution will neither be optimal
 * nor even a valid solution. In addition, the local solver does not deliver ground formulas (or rather, it delivers an
 * empty set of ground formulas). It is, however, really fast and useful during learning.
 *
 * @author Sebastian Riedel
 */
public class LocalSolver implements Solver {

  private GroundFormulas formulas;
  private Model model;
  private LocalFeatures features;
  private LocalFeatureExtractor extractor;
  private Scores scores;
  private GroundAtoms atoms;
  private Weights weights;
  private boolean scoresSet;
  private Profiler profiler = new NullProfiler();

  public LocalSolver() {
  }

  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    formulas = new GroundFormulas(model, weights);
    features = new LocalFeatures(model, weights);
    extractor = new LocalFeatureExtractor(model, weights);
    scores = new Scores(model, weights);
    atoms = model.getSignature().createGroundAtoms();
  }

  /**
   * The solver maintains a profiler that can count the number of (abstract) operations performed during solving. The
   * default profiler does not do any profiling ({@link thebeast.util.NullProfiler}.
   *
   * @return the profiler this solver is using.
   */
  public Profiler getProfiler() {
    return profiler;
  }


  /**
   * Defines a new problem by setting the current observation. The next call to any solve method will use this
   * observation as input.
   *
   * @param atoms the ground atoms to use as observation. Only predicates defined as "observation" are used.
   */
  public void setObservation(GroundAtoms atoms) {
    scoresSet = false;
    //this.atoms.clear(model.getHiddenPredicates());
    this.atoms.load(atoms, model.getObservedPredicates());
  }

  /**
   * This method can be used to provide a user-defined set of local scores for hidden ground atoms. Note that these
   * scores will be forgotten once a new observation is specified with {@link LocalSolver#setObservation(thebeast.pml.GroundAtoms)}.
   *
   * @param scores the scores to use.
   */
  public void setScores(Scores scores) {
    this.scores.load(scores);
    //ilp = new IntegerLinearProgram(model, weights, ilpSolver);
    scoresSet = true;
  }


  public void solve() {
    profiler.start("solve");
    if (!scoresSet) score();
    atoms.load(scores.greedySolve(0.0), model.getHiddenPredicates());
    formulas.clear();
    profiler.end();
  }


  private void score() {
    profiler.start("scoring");
    profiler.start("extract");
    extractor.extract(atoms, features);
    profiler.end();
    profiler.start("score");
    scores.score(features, weights);
    profiler.end();
    scoresSet = true;
    profiler.end();
  }


  public GroundAtoms getBestAtoms() {
    return atoms;
  }

  public GroundFormulas getBestFormulas() {
    return formulas;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
  }


  public List<GroundAtoms> getCandidateAtoms() {
    return Collections.singletonList(atoms);
  }

  public List<GroundFormulas> getCandidateFormulas() {
    return Collections.singletonList(formulas);
  }

  public void setProperty(PropertyName name, Object value) {
  }

  public Object getProperty(PropertyName name) {
    if ("scores".equals(name.getHead()))
      return scores;
    if ("formulas".equals(name.getHead()))
      return formulas;
    if ("features".equals(name.getHead()))
      return features.toVerboseString();
    if ("profiler".equals(name.getHead()))
      return profiler;
    return null;
  }

}
