package thebeast.pml.solve;

import thebeast.pml.*;
import thebeast.pml.formula.FactorFormula;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;
import thebeast.util.TreeProfiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A PML Solver based on the Cutting Plane algorithm and column generation.
 *
 * @author Sebastian Riedel
 */
public class CuttingPlaneSolver implements Solver {

  private IntegerLinearProgram ilp;
  private GroundFormulas formulas;
  private int maxIterations = 10;
  private Model model;
  private LocalFeatures features;
  private LocalFeatureExtractor extractor;
  private Scores scores;
  private GroundAtoms atoms;
  private Weights weights;
  private int iteration;
  private boolean done, scoresSet, initSet, updated, deterministicFirst;
  public ILPSolver ilpSolver;
  private Profiler profiler = new NullProfiler();
  private boolean enforceIntegers;

  private LinkedList<GroundAtoms> candidateAtoms = new LinkedList<GroundAtoms>();
  private LinkedList<GroundFormulas> candidateFormulas = new LinkedList<GroundFormulas>();

  public CuttingPlaneSolver() {
    //ilpSolver = new ILPSolverLpSolve();
    ilpSolver = new ILPSolverOsi();
  }

  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    ilp = new IntegerLinearProgram(model, weights, ilpSolver);
    ilp.setProfiler(profiler);
    formulas = new GroundFormulas(model, weights);
    features = new LocalFeatures(model, weights);
    extractor = new LocalFeatureExtractor(model, weights);
    scores = new Scores(model, weights);
    atoms = model.getSignature().createGroundAtoms();
  }


  /**
   * The cutting plane solve might not need as many iterations as specified through
   * {@link CuttingPlaneSolver#setMaxIterations(int)} or as argument of
   * {@link CuttingPlaneSolver#solve(int)}. This method returns the actual
   * number of iterations that were needed.
   *
   * @return the number of iterations the solver did last time {@link CuttingPlaneSolver#solve(int)} or
   *         {@link CuttingPlaneSolver#solve()} was called.
   */
  public int getIterationCount() {
    return iteration;
  }

  /**
   * Return the maximum number of cutting plane iterations. The solver might use less if
   * feasibility is reached before.
   *
   * @return the maximum number of cutting plane iterations.
   */
  public int getMaxIterations() {
    return maxIterations;
  }

  /**
   * Set the maximum number of cutting plane iterations. The solver might use less if
   * feasibility is reached before.
   *
   * @param maxIterations the maximum number of cutting plane iterations.
   */
  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }


  /**
   * The solver maintains a profiler that can count the number of (abstract) operations
   * performed during solving. The default profiler does not do any profiling ({@link NullProfiler}.
   *
   * @return the profiler this solver is using.
   */
  public Profiler getProfiler() {
    return profiler;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
    if (ilp != null) ilp.setProfiler(profiler);
  }

  public void setILPSolver(ILPSolver solver) {
    ilp.setSolver(solver);
  }

  /**
   * Defines a new problem by setting the current observation. The next call to any solve method will use this
   * observation as input.
   *
   * @param atoms the ground atoms to use as observation. Only predicates defined as "observation" are used.
   */
  public void setObservation(GroundAtoms atoms) {
    done = false;
    scoresSet = false;
    initSet = false;
    updated = false;
    //this.atoms.clear(model.getHiddenPredicates());
    this.atoms.load(atoms, model.getObservedPredicates());
  }

  /**
   * This method can be used to provide a user-defined set of local scores for hidden ground atoms. Note that
   * these scores will be forgotten once a new observation is specified with
   * {@link CuttingPlaneSolver#setObservation(GroundAtoms)}.
   *
   * @param scores the scores to use.
   */
  public void setScores(Scores scores) {
    done = false;
    this.scores.load(scores);
    //ilp = new IntegerLinearProgram(model, weights, ilpSolver);
    ilp.init(this.scores);
    scoresSet = true;
  }


  private void update() {
    profiler.start("update");

    profiler.start("formulas");
    formulas.update(atoms);
    profiler.end();

    profiler.start("ilp.update");
    ilp.update(formulas, atoms);
    profiler.end();

    updated = true;

    profiler.end();
  }

  /**
   * Solves the current problem with the given number of iterations or less if optimal before. If the solver has
   * a initial guess (either from the last time this method was called or through external specification) this
   * guess is used as a starting point. If not a greedy solution is used as a starting point.
   *
   * @param maxIterations the maximum number iterations to use (less if optimality is reached before).
   */
  public void solve(int maxIterations) {
    profiler.start("solve");

    candidateAtoms.clear();
    candidateFormulas.clear();
    iteration = 0;
    if (!scoresSet) score();
    if (!initSet) initSolution();
    if (deterministicFirst) {
      deterministicFirst();      
    } else {
      update();
      candidateAtoms.add(new GroundAtoms(atoms));
      candidateFormulas.add(new GroundFormulas(formulas));
    }

    profiler.start("iterations");
    while (ilp.changed() && iteration < maxIterations) {
      profiler.start("ilp.solve");
      ilp.solve(atoms);
      profiler.end();
      ++iteration;
      update();
      candidateAtoms.add(0, new GroundAtoms(atoms));
      candidateFormulas.add(0, new GroundFormulas(formulas));
      if (enforceIntegers && !ilp.changed() && ilp.isFractional())
        ilp.enforceIntegerSolution();
    }
    profiler.end();

    done = ilp.changed();

    profiler.end();
  }

  private void deterministicFirst() {
    profiler.start("deterministic");

    profiler.start("update det");
    formulas.updateDeterministic(atoms);
    profiler.end();
    //System.out.println(formulas);

    profiler.start("update det ilp");
    ilp.update(formulas,atoms);
    profiler.end();

    profiler.start("load");
    candidateAtoms.add(new GroundAtoms(atoms));
    candidateFormulas.add(new GroundFormulas(formulas));
    profiler.end();

    if (ilp.changed()){
      ilp.solve(atoms);
      update();
      candidateAtoms.add(0, new GroundAtoms(atoms));
      candidateFormulas.add(0, new GroundFormulas(formulas));
    } else{
      formulas.update(atoms, model.getNondeterministicFormulas());
      ilp.update(formulas, atoms);
    }
    profiler.end();
  }

  /**
   * Calls {@link CuttingPlaneSolver#solve(int)} with the maximum number of iterations defined by
   * {@link CuttingPlaneSolver#setMaxIterations(int)}.
   */
  public void solve() {
    solve(maxIterations);
  }

  private void initSolution() {
    profiler.start("greedy", 0);
    atoms.load(scores.greedySolve(0.0), model.getHiddenPredicates());
    initSet = true;
    profiler.end();
  }

  /**
   * Set a starting point for the cutting plane solver.
   *
   * @param atoms a collection of ground atoms (needs to have hidden atoms).
   */
  public void setInititalSolution(GroundAtoms atoms) {
    this.atoms.load(atoms, model.getHiddenPredicates());
    initSet = true;
    updated = false;
  }

  private void score() {
    profiler.start("scoring");
    profiler.start("extract");
    extractor.extract(atoms, features);
    profiler.end();
    profiler.start("score");
    scores.score(features, weights);
    profiler.end();
    profiler.start("ilp.init");
    ilp.init(scores);
    profiler.end();
    scoresSet = true;
    profiler.end();
  }

  public ILPSolver getILPSolver() {
    return ilp.getSolver();
  }


  public GroundAtoms getBestAtoms() {
    return atoms;
  }

  public GroundFormulas getBestFormulas() {
    return formulas;
  }

  public List<GroundAtoms> getCandidateAtoms() {
    return new ArrayList<GroundAtoms>(candidateAtoms);
  }

  public List<GroundFormulas> getCandidateFormulas() {
    return new ArrayList<GroundFormulas>(candidateFormulas);
  }

  public boolean isDone() {
    return done;
  }


  /**
   * Setting this property to true ensures that final solutions will always be integer.
   *
   * @param enforceIntegers true iff integer solutions should be enforced.
   */
  public void setEnforceIntegers(boolean enforceIntegers) {
    this.enforceIntegers = enforceIntegers;
  }


  public boolean isDeterministicFirst() {
    return deterministicFirst;
  }

  public void setDeterministicFirst(boolean deterministicFirst) {
    this.deterministicFirst = deterministicFirst;
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("ilp"))
      ilp.setProperty(name.getTail(), value);
    if (name.getHead().equals("maxIterations"))
      setMaxIterations((Integer) value);
    if (name.getHead().equals("integer"))
      setEnforceIntegers((Boolean) value);
    if (name.getHead().equals("deterministicFirst"))
      setDeterministicFirst((Boolean) value);
    if (name.getHead().equals("profile"))
      setProfiler(((Boolean) value) ? new TreeProfiler() : new NullProfiler());
  }

  public Object getProperty(PropertyName name) {
    if (name.getHead().equals("ilp")) {
      if (name.getTail() == null)
        return ilp.toLpSolveFormat();
      else
        return ilp.getProperty(name.getTail());
    }
    if ("scores".equals(name.getHead()))
      return scores;
    if ("formulas".equals(name.getHead()))
      return formulas;
    if ("features".equals(name.getHead()))
      return PropertyName.getProperty(features,name.getTail());
    if ("profiler".equals(name.getHead()))
      return profiler;
    return null;
  }

  public IntegerLinearProgram getILP() {
    return ilp;
  }
}
