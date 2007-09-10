package thebeast.pml.solve;

import thebeast.pml.*;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.solve.ilp.ILPSolverLpSolve;
import thebeast.pml.solve.ilp.IntegerLinearProgram;
import thebeast.pml.solve.weightedsat.MaxWalkSat;
import thebeast.pml.solve.weightedsat.WeightedSatProblem;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;
import thebeast.util.TreeProfiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * A PML Solver based on the Cutting Plane algorithm and column generation.
 *
 * @author Sebastian Riedel
 */
public class CuttingPlaneSolver implements Solver {

  private PropositionalModel propositionalModel;
  private GroundFormulas formulas;
  private GroundFormulas firstFormulas;
  private int maxIterations = 100;
  private Model model;
  private LocalFeatures features;
  private LocalFeatureExtractor extractor;
  private Scores scores;
  private GroundAtoms atoms;

  private Weights weights;
  private int iteration;
  private boolean done, scoresSet, initSet;
  private Profiler profiler = new NullProfiler();
  private boolean enforceIntegers;

  private long timeout = Long.MAX_VALUE; //10000;

  private int maxOrder = Integer.MAX_VALUE;

  private boolean printHistory = false;
  private boolean showIterations = false;

  private ArrayList<GroundAtoms> candidateAtoms = new ArrayList<GroundAtoms>();
  private ArrayList<GroundFormulas> candidateFormulas = new ArrayList<GroundFormulas>();
  private ArrayList<Integer> candidateOrders = new ArrayList<Integer>();
  private HashSet<Integer> completelyInspected = new HashSet<Integer>();
  private Stack<GroundAtoms> holderAtoms = new Stack<GroundAtoms>();
  private Stack<GroundFormulas> holderFormulas = new Stack<GroundFormulas>();

  private HashSet<FactorFormula> groundAll = new HashSet<FactorFormula>();

  private HashMap<Integer, FactorSet> factorSets = new HashMap<Integer, FactorSet>();
  private HashMap<FactorFormula, Integer> factor2order = new HashMap<FactorFormula, Integer>();
  private ArrayList<FactorSet> orderedFactors = new ArrayList<FactorSet>();

  /**
   * Creates a new solver that uses ILP as propositional model and LpSolve as ILP solver.
   */
  public CuttingPlaneSolver() {
    //ilpSolver = new ILPSolverLpSolve();
    this(new IntegerLinearProgram(new ILPSolverLpSolve()));
  }

  /**
   * Creates a new solver using the given propositional model to represent and solve the grounded networks.
   *
   * @param propositionalModel the model the solve will use to represent and in turn solve the partially grounded
   *                           networks it creates during its iterations.
   */
  public CuttingPlaneSolver(PropositionalModel propositionalModel) {
    this.propositionalModel = propositionalModel;
    propositionalModel.setProfiler(profiler);
  }

  /**
   * Defines whether the given formula should be grounded in advance. Note that if there is one more formulas which have
   * to be grounded in advance, the solver won't do an initial greedy step. The first problem is then solved using the
   * propositional model/solver.
   *
   * @param formula     the formula to be grounded in advance (or not)
   * @param fullyGround true if the formula should grounded in advance, false otherwise.
   */
  public void setFullyGround(FactorFormula formula, boolean fullyGround) {
    if (formula == null) throw new RuntimeException("formula must not be null");
    if (fullyGround) {
      groundAll.add(formula);
      //remove formula from ordered factors
      int order = factor2order.remove(formula);
      FactorSet set = factorSets.get(order);
      set.remove(formula);
      if (set.size() == 0)
        factorSets.remove(order);
    }
    else
      groundAll.remove(formula);
    formulas.setFullyGround(formula, fullyGround);
    firstFormulas.setFullyGround(formula, fullyGround);
    propositionalModel.setFullyGround(formula, fullyGround);
  }

  /**
   * Defines whether all formulas should be grounded in advance, or none
   *
   * @param fullyGroundAll if true all formulas should be grounded in advance, if false none are.
   */
  public void setFullyGroundAll(boolean fullyGroundAll) {
    for (FactorFormula formula : model.getGlobalFactorFormulas())
      setFullyGround(formula, fullyGroundAll);
  }

  /**
   * Configures this solver to work with the given model and weights
   *
   * @param model   the model the solver should use
   * @param weights the weights the solver should use
   */
  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    propositionalModel.configure(model, weights);
    formulas = new GroundFormulas(model, weights);
    formulas.setProfiler(profiler);
    firstFormulas = new GroundFormulas(model, weights);
    features = new LocalFeatures(model, weights);
    extractor = new LocalFeatureExtractor(model, weights);
    scores = new Scores(model, weights);
    atoms = model.getSignature().createGroundAtoms();
    atoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
    groundAll.clear();
    factor2order.clear();
    factorSets.clear();
    orderedFactors.clear();

    for (FactorFormula formula : model.getGlobalFactorFormulas())
      setOrder(formula, 0);
  }


  /**
   * The cutting plane solve might not need as many iterations as specified through {@link
   * CuttingPlaneSolver#setMaxIterations(int)} or as argument of {@link CuttingPlaneSolver#solve(int)}. This method
   * returns the actual number of iterations that were needed.
   *
   * @return the number of iterations the solver did last time {@link CuttingPlaneSolver#solve(int)} or {@link
   *         CuttingPlaneSolver#solve()} was called.
   */
  public int getIterationCount() {
    return iteration;
  }

  /**
   * Return the maximum number of cutting plane iterations. The solver might use less if feasibility is reached before.
   *
   * @return the maximum number of cutting plane iterations.
   */
  public int getMaxIterations() {
    return maxIterations;
  }

  /**
   * Set the maximum number of cutting plane iterations. The solver might use less if feasibility is reached before.
   *
   * @param maxIterations the maximum number of cutting plane iterations.
   */
  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
    System.out.println("maxIterations = " + this.maxIterations);
  }


  /**
   * The solver maintains a profiler that can count the number of (abstract) operations performed during solving. The
   * default profiler does not do any profiling ({@link NullProfiler}.
   *
   * @return the profiler this solver is using.
   */
  public Profiler getProfiler() {
    return profiler;
  }

  /**
   * Sets the profiler for this solver. The profiler will never be called in inner loops etc but only before and after
   * relatively coarse subroutines such as "find violated constraints" etc.
   *
   * @param profiler the profiler to use
   */
  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
    if (propositionalModel != null) propositionalModel.setProfiler(profiler);
    if (formulas != null) formulas.setProfiler(profiler);
  }


  /**
   * Sets the propositional model to represent and solve partially grounded networks.
   *
   * @param propositionalModel the propositional model to be used for grounding.
   */
  public void setPropositionalModel(PropositionalModel propositionalModel) {
    this.propositionalModel = propositionalModel;
    propositionalModel.configure(model, weights);
    propositionalModel.setProfiler(profiler);
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
//    ((ILPSolverLpSolve)((IntegerLinearProgram)propositionalModel).getSolver()).delete();
//    propositionalModel = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());
    //this.atoms.clear(model.getHiddenPredicates());
    this.atoms.clear(model.getHiddenPredicates());
    this.atoms.clear(model.getInstancePredicates());
    //this.atoms.clear(model.getGlobalPredicates());
    this.atoms.load(atoms, model.getObservedPredicates());
    //todo: what to do with this?
    //this.atoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
    completelyInspected.clear();

  }

  /**
   * This method can be used to provide a user-defined set of local scores for hidden ground atoms. Note that these
   * scores will be forgotten once a new observation is specified with {@link CuttingPlaneSolver#setObservation(GroundAtoms)}.
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


  /**
   * This method extracts ground formulas from the current solution and updates the propositional model accordingly.
   *
   * @param factors the collection of factors to use for updating.
   */
  private void update(Collection<FactorFormula> factors) {
    profiler.start("update");

    profiler.start("formulas");
    //System.out.println("Grounding");
    formulas.update(atoms, factors);
    profiler.end();

    //System.out.println(formulas);

    profiler.start("updatemodel");
    //System.out.println("Transfer");
    propositionalModel.update(formulas, atoms, factors);
    profiler.end();

    //System.out.println(ilp.toLpSolveFormat());

    profiler.end();
  }

  private void createFullyGroundedFormulas() {
    profiler.start("update");

    profiler.start("formulas");
    //System.out.println("Grounding");
    formulas.update(atoms, groundAll);
    profiler.end();

    //System.out.println(formulas);

    profiler.start("ilp.update");
    //System.out.println("Transfer");
    propositionalModel.update(formulas, atoms,groundAll);
    profiler.end();

    //System.out.println(ilp.toLpSolveFormat());

    profiler.end();
  }


  /**
   * @return true if the solver calculates a first solution purely based on local scores by itself, false if the first
   *         step uses constraints.
   */
  public boolean doesOwnLocalSearch() {
    return groundAll.isEmpty();
  }


  /**
   * Solves the current problem with the given number of iterations or less if optimal before. If the solver has a
   * initial guess (either from the last time this method was called or through external specification) this guess is
   * used as a starting point. If not a greedy solution is used as a starting point.
   *
   * @param maxIterations the maximum number iterations to use (less if optimality is reached before).
   */
  public void solve(int maxIterations) {

    long start = System.currentTimeMillis();

    formulas.init();
    firstFormulas.init();
    profiler.start("solve");

    holderAtoms.addAll(candidateAtoms);
    holderFormulas.addAll(candidateFormulas);
    candidateAtoms.clear();
    candidateFormulas.clear();
    candidateOrders.clear();
    iteration = 0;
    if (!scoresSet) score();
    propositionalModel.setClosure(scores.getClosure());

    int order = 0;

    if (groundAll.isEmpty()) {
      //System.out.println(ground);
      if (!initSet) initSolution();
      ++iteration;
      //update(factors);
      order = inspect();
      addCandidate(order);
      if (showIterations) System.out.print("+");
    } else {
      profiler.start("ground-all");
      atoms.clear(model.getHiddenPredicates());
      propositionalModel.buildLocalModel();
      createFullyGroundedFormulas();
      profiler.end();
      //addCandidate(groundAllOrder);
    }
    profiler.start("iterations");
    while (propositionalModel.changed() && iteration < maxIterations && order <= maxOrder) {
      //System.out.println(iteration + " of " + maxIterations);
      if (System.currentTimeMillis() - start > timeout) {
        System.out.println("timeout");
        break;
      }
      profiler.start("solvemodel");
      //System.out.println(((IntegerLinearProgram)propositionalModel).getConstraints().value());
      propositionalModel.solve(atoms);
      //System.out.println("solved!");
      profiler.end();
      ++iteration;
      order = inspect();
      addCandidate(order);
      if (enforceIntegers && !propositionalModel.changed() && propositionalModel.isFractional()) {
        propositionalModel.enforceIntegerSolution();
      }
      if (showIterations) System.out.print("+");
    }
    profiler.end();

    done = propositionalModel.changed();

    profiler.end();
    //System.out.println("!");

    if (printHistory) printHistory();

  }


  /**
   * This method returns the set of ground formulas for the given candidate solution index. Note that this method might
   * need to perform some expensive operations since the solver does not need to have a complete set of ground formulas
   * ready. Thus it might need to search for more unsatisfied formulas in order to return a consisten ground formulas
   * object.
   *
   * @param candidate the index of the candidate we want the ground formulas for
   * @return the ground formulas for the given candidate.
   */
  public GroundFormulas getCandidateFormulas(int candidate) {
    int order = candidateOrders.get(candidate);
    GroundFormulas formulas = candidateFormulas.get(candidate);
    if (completelyInspected.contains(candidate)) return formulas;
    //we know that the formulas have been inspect up to <order> so we need to inspect from <order> + 1
    //update if needed
    while (order != Integer.MAX_VALUE && ++order < orderedFactors.size()) {
      FactorSet set = orderedFactors.get(order);
      formulas.update(candidateAtoms.get(candidate), set);
    }
    completelyInspected.add(candidate);
    return formulas;
  }

  /**
   * Returns the candidate solution with the specified index.
   *
   * @param candidate the index of the candidate
   * @return a set of ground atoms representing the candidate
   */
  public GroundAtoms getCandidateAtoms(int candidate) {
    return candidateAtoms.get(candidate);
  }

  /**
   * Returns the order of a candidate solution. The order of a candidate solution is the order of the newly found
   * formula with lowest order. A formula f is newly found at candidate i>0 if i contains a ground version of f that is
   * not contained in any candidate in 0 ... i-1. For i = 0 each formula that has been found in i = 0 is newly found.
   *
   * @param candidate the candidate index
   * @return the order of the given candidate.
   */
  public int getCandidateOrder(int candidate) {
    return candidateOrders.get(candidate);
  }

  /**
   * Returns the number of candidates the solver generated on the way to its final solution. These might be used for
   * learning.
   *
   * @return the number of candidates the solver generated on the way.
   */
  public int getCandidateCount() {
    return candidateAtoms.size();
  }

  /**
   * Inspects the current solutions, updates formulas and propositional model and returns the lowest order of formulas
   * that are violated in the current solution.
   *
   * @return the order of the set of ground formulas of the last solution.
   */
  private int inspect() {
    if (orderedFactors.size() == 0) return Integer.MAX_VALUE;
    int order = 0;
    do {
      //System.out.println("-------------------");
      //System.out.println("Order: " + order);
      FactorSet set = orderedFactors.get(order);
      //System.out.println(set);
      update(set);
      //if (order == 0) System.out.println(formulas.getNewGroundFormulas(set.iterator().next()).value());
      //System.out.println(propositionalModel);

      if (propositionalModel.changed()) {
        return order;
      }
      ++order;
    } while (order < orderedFactors.size());
    return Integer.MAX_VALUE;
  }

  private void addCandidate(int order) {
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
    candidateOrders.add(0, order);
  }

  /**
   * Calls {@link CuttingPlaneSolver#solve(int)} with the maximum number of iterations defined by {@link
   * CuttingPlaneSolver#setMaxIterations(int)}.
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
  }

  private void score() {
    profiler.start("scoring");
    profiler.start("extract");
    extractor.extract(atoms, features);
    profiler.end();
    profiler.start("score");
    scores.score(features, atoms);
    profiler.end();
    profiler.start("ilp.init");
    propositionalModel.init(scores);
    profiler.end();
    scoresSet = true;
    profiler.end();
  }

  /**
   * This method returns the final solution after the last iteration.
   *
   * @return ground atoms storing the final solution after the last iteration.
   */
  public GroundAtoms getBestAtoms() {
    return atoms;
  }

  public GroundFormulas getBestFormulas() {
    return formulas;
  }


  /**
   * The solver might be limited to only do a fixed number of iterations. If the solver has to stop before the
   * propositional model is not changing anymore, it is not "done".
   *
   * @return true if the propositional model has not changed in the last iteration, false if the solver had to be
   *         stopped before that.
   */
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


  /**
   * Defines an ordering on the set of factor formulas. This is ordering is used to determine when the solver first
   * inspects the solutions for violations of the given factor formula. If a factor A has the order k and another factor
   * B has the order l > k then we will only start to look for violations of B when no no more new violations of A can
   * be found. By default each formula has the the order 0.
   *
   * @param factorFormula the formula to give an order to.
   * @param order         the order of the formula.
   */
  public void setOrder(FactorFormula factorFormula, int order) {
    Integer oldOrder = factor2order.get(factorFormula);
    if (oldOrder != null) {
      FactorSet factorSet = factorSets.get(oldOrder);
      factorSet.remove(factorFormula);
      factor2order.remove(factorFormula);
      if (factorSet.size() == 0) {
        factorSets.remove(oldOrder);
        orderedFactors.remove(factorSet);
      }
    }
    FactorSet set = factorSets.get(order);
    if (set == null) {
      set = new FactorSet(order);
      factorSets.put(order, set);
      orderedFactors.add(set);
      Collections.sort(orderedFactors);
    }
    factor2order.put(factorFormula, order);
    set.add(factorFormula);
  }

  /**
   * The solver usually runs until convergence or until a specified order is achieved. The timeout time specifies the
   * time when solver stops even though none of the above criteria is fulfilled.
   *
   * @return the time in milliseconds until the solver is forced to stop.
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * The solver usually runs until convergence or until a specified order is achieved. The timeout time specifies the
   * time when solver stops even though none of the above criteria is fulfilled.
   *
   * @param timeout the time in milliseconds until the solver is forced to stop.
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }


  /**
   * The solver can be told to stop when its solution reaches a certain order. For example, if all deterministic
   * formulas have order 0 and all global nondeterministic formulas have order 1, by setting the maxOrder to 0 we stop
   * once all hard constraints are fulfilled. By default this is Integer.MAX_VALUE.
   *
   * @return the order at which the solver stops.
   */
  public int getMaxOrder() {
    return maxOrder;
  }

  /**
   * The solver can be told to stop when its solution reaches a certain order. For example, if all deterministic
   * formulas have order 0 and all global nondeterministic formulas have order 1, by setting the maxOrder to 0 we stop
   * once all hard constraints are fulfilled. By default this is Integer.MAX_VALUE.
   *
   * @param maxOrder the order at which the solver stops.
   */
  public void setMaxOrder(int maxOrder) {
    this.maxOrder = maxOrder;
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("model")) {
      if (name.isTerminal()) {
        if ("ilp".equals(value))
          setPropositionalModel(new IntegerLinearProgram(new ILPSolverLpSolve()));
        else if ("sat".equals(value))
          setPropositionalModel(new WeightedSatProblem(new MaxWalkSat()));
      } else
        propositionalModel.setProperty(name.getTail(), value);

    } else if (name.getHead().equals("ground")) {
      String factorName = name.getTail().getHead();
      FactorFormula formula = model.getFactorFormula(factorName);
      if (formula == null)
        throw new RuntimeException("There is no factor with name " + name.getTail().getHead());
      setFullyGround(formula, (Boolean) value);
    } else if (name.getHead().equals("order")) {
      String factorName = name.getTail().getHead();
      FactorFormula formula = model.getFactorFormula(factorName);
      if (formula == null)
        throw new RuntimeException("There is no factor with name " + name.getTail().getHead());
      setOrder(formula, (Integer) value);
    } else if (name.getHead().equals("maxIterations"))
      setMaxIterations((Integer) value);
    else if (name.getHead().equals("timeout"))
      setTimeout((Integer) value);
    else if (name.getHead().equals("maxOrder"))
      setMaxOrder((Integer) value);
    else if (name.getHead().equals("integer"))
      setEnforceIntegers((Boolean) value);
    else if (name.getHead().equals("showIterations"))
      setShowIterations((Boolean) value);
    else if (name.getHead().equals("history"))
      setPrintHistory((Boolean) value);
    else if (name.getHead().equals("groundAll"))
      setFullyGroundAll((Boolean) value);
    else if (name.getHead().equals("profile"))
      setProfiler(((Boolean) value) ? new TreeProfiler() : new NullProfiler());
    else if (name.getHead().equals("profiler"))
      if (!name.isTerminal())
        profiler.setProperty(name.getTail(), value);
  }

  private void setShowIterations(Boolean aBoolean) {
    showIterations = aBoolean;
  }

  private void setPrintHistory(boolean printHistory) {
    this.printHistory = printHistory;
  }


  public PropositionalModel getPropositionalModel() {
    return propositionalModel;
  }

  public Object getProperty(PropertyName name) {
    if (name.getHead().equals("model")) {
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

    //GroundAtomsPrinter printer = new CoNLL00SentencePrinter();
    //GroundAtomsPrinter printer = new SemtagPrinter();
    GroundAtoms last = candidateAtoms.get(candidateAtoms.size() - 1);
    out.println(">>>>>> Atoms <<<<<<");
    for (UserPredicate hidden : model.getHiddenPredicates())
      out.println(last.getGroundAtomsOf(hidden));
    out.println(">>>>>> Formulas <<<<<<");
    out.println(candidateFormulas.get(candidateAtoms.size() - 1));
    Evaluation evaluation = new Evaluation(model);
    int candidate = candidateAtoms.size() - 2;
    while (candidate >= 0) {
      GroundAtoms current = candidateAtoms.get(candidate);
      GroundFormulas form = candidateFormulas.get(candidate);
      evaluation.evaluate(current, last);
      out.println(">>>>>> Changes <<<<<<");
      out.println(evaluation);
      out.println(">>>>>> Atoms <<<<<<");
      for (UserPredicate hidden : model.getHiddenPredicates())
        out.println(current.getGroundAtomsOf(hidden));
      out.println(">>>>>> Formulas <<<<<<");
      out.println(form);
      //printer.print(current, out);
      last = current;
      --candidate;
    }
  }


  /**
   * The solver has to extract local features for the given observation. This method allows client to inspect the local
   * features from the last instance processed.
   *
   * @return the local features for the last solved problem.
   */
  public LocalFeatures getLocalFeatures() {
    return features;
  }

  /**
   * A FactorSet is a set of factors along with a number that determines when the factors of the set are first checked
   * for in the current solution.
   */
  private static class FactorSet extends HashSet<FactorFormula> implements Comparable<FactorSet> {
    public final int order;

    private boolean allDeterministic = true;

    public FactorSet(int order) {
      this.order = order;
    }


    public boolean isAllDeterministic() {
      return allDeterministic;
    }

    public boolean add(FactorFormula factorFormula) {
      if (!factorFormula.isDeterministic())
        allDeterministic = false;
      return super.add(factorFormula);
    }


    public boolean remove(Object o) {
      boolean result = super.remove(o);
      allDeterministic = true;
      for (FactorFormula f : this) {
        if (!f.isDeterministic()) {
          allDeterministic = false;
          break;
        }
      }
      return result;
    }

    public int compareTo(FactorSet factorSet) {
      return order - factorSet.order;
    }
  }

}
