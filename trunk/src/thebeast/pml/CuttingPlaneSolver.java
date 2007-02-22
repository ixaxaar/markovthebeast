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
  private Scores scores;
  private GroundAtoms atoms;
  private Weights weights;
  private int iteration;
  private boolean done, scoresSet, initSet, updated;

  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());
    formulas = new GroundFormulas(model, weights);
    features = new LocalFeatures(model, weights);
    scores = new Scores(model, weights);
    atoms = model.getSignature().createGroundAtoms();
  }


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

  public void setObservation(GroundAtoms atoms) {
    done = false;
    scoresSet = false;
    initSet = false;
    updated = false;
    this.atoms.load(atoms, model.getObservedPredicates());
  }

  public void setScores(Scores scores) {
    done = false;
    this.scores.load(scores);
    ilp.init(this.scores);
    scoresSet = true;
  }


  private void update() {
    formulas.update(atoms);
    ilp.update(formulas, atoms);
    updated = true;
  }

  public void solve(int maxIterations) {
    if (!scoresSet) score();
    if (!initSet) initSolution();
    if (!updated) update();
    iteration = 0;
    while (ilp.changed() && iteration < maxIterations) {
      ilp.solve(atoms);
      update();
      ++iteration;
    }
    done = ilp.changed();
  }

  public void solve() {
    solve(maxIterations);
  }

  public void solve(GroundAtoms init, int maxIterations) {
    initSolution(init);
    if (!scoresSet) score();
    if (!updated) update();
    iteration = 0;
    while (ilp.changed() && iteration < maxIterations) {
      ilp.solve(atoms);
      update();
      ++iteration;
    }
    done = ilp.changed();
  }

  private void initSolution() {
    atoms.load(scores.greedySolve(0.0), model.getHiddenPredicates());
    initSet = true;
  }

  private void initSolution(GroundAtoms atoms) {
    this.atoms.load(atoms, model.getHiddenPredicates());
    initSet = true;
    updated = false;
  }

  private void score() {
    features.extract(this.atoms);
    scores.score(features, weights);
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
      ilp.getSolver().setProperty(name.getTail(), value);
    if (name.getHead().equals("maxIterations"))
      setMaxIterations((Integer)value);
  }

  public Object getProperty(PropertyName name) {
    if (name.getHead().equals("ilp"))
      return ilp.getSolver().getProperty(name.getTail());
    return null;
  }
}
