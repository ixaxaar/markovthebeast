package thebeast.pml;

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
  private boolean done, scoresSet, initSet, updated;
  public ILPSolverLpSolve ilpSolver;


  public CuttingPlaneSolver() {
    ilpSolver = new ILPSolverLpSolve();    
  }

  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    ilp = new IntegerLinearProgram(model, weights, ilpSolver);
    formulas = new GroundFormulas(model, weights);
    features = new LocalFeatures(model, weights);
    extractor = new LocalFeatureExtractor(model, weights);
    scores = new Scores(model, weights);
    atoms = model.getSignature().createGroundAtoms();
  }


  /**
   * The cutting plane solve might not need as many iterations as specified through
   * {@link thebeast.pml.CuttingPlaneSolver#setMaxIterations(int)} or as argument of
   * {@link thebeast.pml.CuttingPlaneSolver#solve(int)}. This method returns the actual
   * number of iterations that were needed.
   *
   * @return the number of iterations the solver did last time {@link thebeast.pml.CuttingPlaneSolver#solve(int)} or
   *         {@link CuttingPlaneSolver#solve()} was called.
   */
  public int getIterationCount() {
    return iteration;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
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
   * {@link thebeast.pml.CuttingPlaneSolver#setObservation(GroundAtoms)}.
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
    formulas.update(atoms);
    //System.out.println(formulas);    
    ilp.update(formulas, atoms);
    updated = true;
  }

  /**
   * Solves the current problem with the given number of iterations or less if optimal before. If the solver has
   * a initial guess (either from the last time this method was called or through external specification) this
   * guess is used as a starting point. If not a greedy solution is used as a starting point.
   *
   * @param maxIterations the maximum number iterations to use (less if optimality is reached before).
   */
  public void solve(int maxIterations) {
    iteration = 0;
    if (!scoresSet) score();
    if (!initSet) initSolution();
    if (!updated) update();
    //System.out.println(atoms);
    //System.out.println(formulas);
    //System.out.println(ilp);
    //System.out.println(ilp.toLpSolveFormat());
    while (ilp.changed() && iteration < maxIterations) {
      //System.out.println(ilp.toLpSolveFormat());
      ilp.solve(atoms);
      //System.out.println(ilp.getResultString());
      //System.out.println(atoms);
      update();
      ++iteration;
      //System.out.println(formulas);
    }
    //System.out.println("Final ILP:\n" + ilp.toLpSolveFormat());
    done = ilp.changed();
  }

  /**
   * Calls {@link thebeast.pml.CuttingPlaneSolver#solve(int)} with the maximum number of iterations defined by
   * {@link thebeast.pml.CuttingPlaneSolver#setMaxIterations(int)}.
   */
  public void solve() {
    solve(maxIterations);
  }

  private void initSolution() {
    atoms.load(scores.greedySolve(0.0), model.getHiddenPredicates());
    //System.out.println(atoms);
    initSet = true;
    //++iteration;
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
    extractor.extract(atoms,features);
    //scores = new Scores(model, weights);
    scores.score(features, weights);
    //System.out.println(scores);
    ilp.init(scores);
    scoresSet = true;
  }

  public ILPSolver getILPSolver() {
    return ilp.getSolver();
  }


  public GroundAtoms getAtoms() {
    return atoms;
  }

  public GroundFormulas getFormulas() {
    return formulas;
  }

  public boolean isDone() {
    return done;
  }


  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("ilp"))
      ilp.setProperty(name.getTail(), value);
    if (name.getHead().equals("maxIterations"))
      setMaxIterations((Integer) value);
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
      return features.toVerboseString();
    return null;
  }

  public IntegerLinearProgram getILP() {
    return ilp;
  }
}
