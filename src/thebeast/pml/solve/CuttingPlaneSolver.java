package thebeast.pml.solve;

import thebeast.pml.*;
import thebeast.pml.corpora.CoNLL00SentencePrinter;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;
import thebeast.util.TreeProfiler;

import java.util.*;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 * A PML Solver based on the Cutting Plane algorithm and column generation.
 *
 * @author Sebastian Riedel
 */
public class CuttingPlaneSolver implements Solver {

  private PropositionalModel propositionalModel;
  private GroundFormulas formulas;
  private GroundFormulas firstFormulas;
  private int maxIterations = 10;
  private Model model;
  private LocalFeatures features;
  private LocalFeatureExtractor extractor;
  private Scores scores;
  private GroundAtoms atoms;
  private GroundAtoms greedyAtoms;
  private GroundFormulas greedyFormulas;

  private Weights weights;
  private int iteration;
  private boolean done, scoresSet, initSet, updated, deterministicFirst;
  private Profiler profiler = new NullProfiler();
  private boolean enforceIntegers;

  private int maxViolationsForNonDeterministic = 1;
  private boolean alternating = false;

  private boolean printHistory = false;

  private LinkedList<GroundAtoms> candidateAtoms = new LinkedList<GroundAtoms>();
  private LinkedList<GroundFormulas> candidateFormulas = new LinkedList<GroundFormulas>();
  private Stack<GroundAtoms> holderAtoms = new Stack<GroundAtoms>();
  private Stack<GroundFormulas> holderFormulas = new Stack<GroundFormulas>();

  public CuttingPlaneSolver() {
    //ilpSolver = new ILPSolverLpSolve();
    this(new IntegerLinearProgram(new ILPSolverLpSolve()));
  }



  public CuttingPlaneSolver(PropositionalModel propositionalModel) {
    this.propositionalModel = propositionalModel;
    propositionalModel.setProfiler(profiler);
  }

  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    propositionalModel.configure(model,weights);
    formulas = new GroundFormulas(model, weights);
    firstFormulas = new GroundFormulas(model, weights);
    features = new LocalFeatures(model, weights);
    extractor = new LocalFeatureExtractor(model, weights);
    scores = new Scores(model, weights);
    atoms = model.getSignature().createGroundAtoms();
    greedyAtoms = model.getSignature().createGroundAtoms();
    greedyFormulas = new GroundFormulas(model, weights);
    atoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
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
    if (propositionalModel != null) propositionalModel.setProfiler(profiler);
    if (formulas != null) formulas.setProfiler(profiler);
  }


  public void setPropositionalModel(PropositionalModel propositionalModel) {
    this.propositionalModel = propositionalModel;
  }


  public GroundAtoms getGreedyAtoms() {
    return greedyAtoms;
  }

  public GroundFormulas getGreedyFormulas() {
    return greedyFormulas;
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
    this.atoms.clear(model.getHiddenPredicates());
    this.atoms.clear(model.getInstancePredicates());
    this.atoms.clear(model.getGlobalPredicates());
    this.atoms.load(atoms, model.getObservedPredicates());
    this.atoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
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
    propositionalModel.init(this.scores);
    scoresSet = true;
  }


  private void update() {
    profiler.start("update");

    profiler.start("formulas");
    formulas.update(atoms);
    profiler.end();

    //System.out.println(formulas);

    profiler.start("ilp.update");
    propositionalModel.update(formulas, atoms);
    profiler.end();

    //System.out.println(ilp.toLpSolveFormat());

    updated = true;

    profiler.end();
  }

  private void updateAlternating() {
    profiler.start("update");

    profiler.start("det formulas");
    formulas.update(atoms, model.getDeterministicFormulas());
    profiler.end();

    //System.out.print("Iteration " + iteration + ": ");
    if (!alternating || formulas.getViolationCount() <= maxViolationsForNonDeterministic) {
      //System.out.println("nondet");
      profiler.start("nondet formulas");
      formulas.update(atoms, model.getNondeterministicFormulas());
      profiler.end();
    } else {
      //System.out.println("det");
    }
    //System.out.println(formulas);

    profiler.start("ilp.update");
    propositionalModel.update(formulas, atoms);
    profiler.end();

    //System.out.println(ilp.toLpSolveFormat());

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
    if (alternating) {
      solveAlternating(maxIterations);
      return;
    }

    /*
    UserPredicate brill = model.getSignature().getUserPredicate("brill");
    System.out.println("atoms.getGroundAtomsOf(brill).containsAtom(\"of\",\"IN\") = " + atoms.getGroundAtomsOf(brill).containsAtom("\"of\"", "\"IN\""));
    */

    formulas.init();
    firstFormulas.init();
    //formulas = new GroundFormulas(model, weights);
    //ilp = new IntegerLinearProgram(model,weights, ilpSolver);

    //System.out.println(formulas);
    //formulas = new GroundFormulas(model, weights);
    profiler.start("solve");

    holderAtoms.addAll(candidateAtoms);
    holderFormulas.addAll(candidateFormulas);
    candidateAtoms.clear();
    candidateFormulas.clear();
    iteration = 0;
    if (!scoresSet) score();
    propositionalModel.setClosure(scores.getClosure());
    if (!initSet) initSolution();
    //new SentencePrinter().print(atoms, System.out);


    if (deterministicFirst) {
      deterministicFirst();
      //new SentencePrinter().print(atoms, System.out);
    } else {
      update();
      setGreedy();
    }

    //new SentencePrinter().print(atoms, System.out);
//    System.out.println("Iteration " + iteration);
//    System.out.println(((IntegerLinearProgram)propositionalModel).toLpSolveFormat());

    profiler.start("iterations");
    //System.out.print(formulas.size() + " -> ");
    //System.out.println(ilp.getNumRows());
    while (propositionalModel.changed() && iteration < maxIterations) {
      profiler.start("ilp.solve");
      propositionalModel.solve(atoms);
      //new SentencePrinter().print(atoms, System.out);

      profiler.end();
      ++iteration;
      update();
//      System.out.println("Iteration " + iteration);
//      System.out.println(((IntegerLinearProgram)propositionalModel).toLpSolveFormat());
      addCandidate();
      if (enforceIntegers && !propositionalModel.changed() && propositionalModel.isFractional()) {
        //System.out.println("fractional");
        propositionalModel.enforceIntegerSolution();
      }
//      if (iteration == 1){
//        System.out.print(formulas.size() + " -> ");
//        System.out.println(ilp.getNumRows());
//        new SentencePrinter().print(atoms,System.out);
//        System.out.println(formulas);
//      }
    }
    //System.out.print(iteration);
    profiler.end();

    done = propositionalModel.changed();

    profiler.end();

    if (printHistory) printHistory();

    //System.out.println(ilp.toLpSolveFormat());
  }

  /**
   * Solves the current problem with the given number of iterations or less if optimal before. If the solver has
   * a initial guess (either from the last time this method was called or through external specification) this
   * guess is used as a starting point. If not a greedy solution is used as a starting point.
   *
   * <p>This version only adds global features/soft constraints if no hard constraints (==less than maxViolationsForNonDeterministic)
   * are violated in the current solution.
   *
   * @param maxIterations the maximum number iterations to use (less if optimality is reached before).
   */
  public void solveAlternating(int maxIterations) {
    //todo: factorize this! use the solve method and parametrize the update function to use

    formulas.init();
    firstFormulas.init();
    //formulas = new GroundFormulas(model, weights);
    //ilp = new IntegerLinearProgram(model,weights, ilpSolver);

    //System.out.println(formulas);
    //formulas = new GroundFormulas(model, weights);
    profiler.start("solve");

    holderAtoms.addAll(candidateAtoms);
    holderFormulas.addAll(candidateFormulas);
    candidateAtoms.clear();
    candidateFormulas.clear();
    iteration = 0;
    if (!scoresSet) score();
    propositionalModel.setClosure(scores.getClosure());
    if (!initSet) initSolution();
    //new SentencePrinter().print(atoms, System.out);

    updateAlternating();
    setGreedy();

    //new SentencePrinter().print(atoms, System.out);
    //System.out.println(ilp.toLpSolveFormat());

    profiler.start("iterations");
    //System.out.print(formulas.size() + " -> ");
    //System.out.println(ilp.getNumRows());
    while (propositionalModel.changed() && iteration < maxIterations) {
      profiler.start("ilp.solve");
      propositionalModel.solve(atoms);
      //new SentencePrinter().print(atoms, System.out);

      profiler.end();
      ++iteration;
      updateAlternating();
      addCandidate();
      if (enforceIntegers && propositionalModel.isFractional()) {
      //if (enforceIntegers && !ilp.changed() && ilp.isFractional()) {
        //System.out.println("fractional");
        propositionalModel.enforceIntegerSolution();
      }
//      if (iteration == 1){
//        System.out.print(formulas.size() + " -> ");
//        System.out.println(ilp.getNumRows());
//        new SentencePrinter().print(atoms,System.out);
//        System.out.println(formulas);
//      }
    }
    //System.out.print(iteration);
    profiler.end();

    done = propositionalModel.changed();

    profiler.end();

    if (printHistory) printHistory();
  }

  private void addCandidate() {
    if (holderAtoms.isEmpty()) {
      candidateAtoms.add(0, new GroundAtoms(atoms));
      candidateFormulas.add(0, new GroundFormulas(formulas));
    } else {
      GroundAtoms atomsToAdd = holderAtoms.pop();
      GroundFormulas formulasToAdd = holderFormulas.pop();
      atomsToAdd.load(atoms);
      formulasToAdd.load(formulas);
      candidateAtoms.add(0, atomsToAdd);
      candidateFormulas.add(0, formulasToAdd);
    }
  }

  private void setGreedy() {
    greedyAtoms.load(atoms);
    greedyFormulas.load(formulas);

  }

  private void deterministicFirst() {
    profiler.start("deterministic");
    //new SentencePrinter().print(atoms, System.out);

    formulas.update(atoms);
    //add greedy solution to candidates
    setGreedy();
    //addCandidate();
    //update the ilp (but only with hard constraints)
    propositionalModel.update(formulas, atoms, model.getDeterministicFormulas());
    //System.out.println(ilp.toLpSolveFormat());
    if (propositionalModel.changed()) {
      //some constraints were violated -> lets solve
      propositionalModel.solve(atoms);
      //ilp.update(formulas,atoms,model.getNondeterministicFormulas());
      //create a new set of ground formulas and a new ilp
      update();
      //add the first solution which takes constraints into account
      addCandidate();
    } else {
      //formulas.update(atoms, model.getNondeterministicFormulas());
      propositionalModel.update(formulas, atoms);
    }

    profiler.end();
    //System.out.println(formulas);


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
    propositionalModel.init(scores);
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

  /**
   * The solver remembers all partial solutions on the way to its final solution. They can be accessed
   * using this method. Note: The solver owns all ground atoms returned by this method and will
   * overwrite them in the next solve-call. If you need these atoms permanently you need to create a
   * copy of them.
   *
   * @return the list of solutions generated "on the way";
   */
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


  public boolean isAlternating() {
    return alternating;
  }

  public void setAlternating(boolean alternating) {
    this.alternating = alternating;
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("ilp"))
      propositionalModel.setProperty(name.getTail(), value);
    if (name.getHead().equals("maxIterations"))
      setMaxIterations((Integer) value);
    if (name.getHead().equals("integer"))
      setEnforceIntegers((Boolean) value);
    if (name.getHead().equals("alternating"))
      setAlternating((Boolean) value);
    if (name.getHead().equals("deterministicFirst"))
      setDeterministicFirst((Boolean) value);
    if (name.getHead().equals("profile"))
      setProfiler(((Boolean) value) ? new TreeProfiler() : new NullProfiler());
  }


  public PropositionalModel getPropositionalModel() {
    return propositionalModel;
  }

  public Object getProperty(PropertyName name) {
    if (name.getHead().equals("ilp")) {
      if (name.getTail() == null)
        return propositionalModel.toString();
      else
        return propositionalModel.getProperty(name.getTail());
    }
    if ("scores".equals(name.getHead()))
      return scores;
    if ("history".equals(name.getHead()))
      return getHistoryString();
    if ("formulas".equals(name.getHead()))
      return formulas;
    if ("features".equals(name.getHead()))
      return PropertyName.getProperty(features, name.getTail());
    if ("profiler".equals(name.getHead()))
      return profiler;
    return null;
  }

 
  public void printHistory() {
    printHistory(System.out);
  }

  public String getHistoryString() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bos);
    printHistory(out);
    return bos.toString();
  }

  public void printHistory(PrintStream out) {
    out.println("=======================================");
    CoNLL00SentencePrinter printer = new CoNLL00SentencePrinter();
    printer.print(greedyAtoms, out);
    GroundAtoms last = greedyAtoms;
    ListIterator<GroundAtoms> iter = candidateAtoms.listIterator(candidateAtoms.size());
    Evaluation evaluation = new Evaluation(model);
    while (iter.hasPrevious()) {
      GroundAtoms current = iter.previous();
      evaluation.evaluate(current, last);
      out.println(evaluation);
      printer.print(current, out);
      last = current;
    }
  }

}
