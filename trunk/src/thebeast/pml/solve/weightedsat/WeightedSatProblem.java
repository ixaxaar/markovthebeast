package thebeast.pml.solve.weightedsat;

import thebeast.pml.*;
import thebeast.pml.solve.PropositionalModel;
import thebeast.pml.formula.FactorFormula;
import thebeast.util.Profiler;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.Heading;
import thebeast.nod.util.TypeBuilder;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.ArrayValue;

import java.util.Collection;
import java.util.HashMap;

/**
 * A WeightedSatProblem represents a set of weighted clauses in CNF.
 *
 * @author Sebastian Riedel
 */
public class WeightedSatProblem implements PropositionalModel {

  private Model model;
  private WeightedSatSolver solver;
  private Scores scores;
  private Weights weights;

  private int oldNumAtoms;

  private HashMap<UserPredicate, RelationVariable>
          atoms = new HashMap<UserPredicate, RelationVariable>(),
          newAtoms = new HashMap<UserPredicate, RelationVariable>();
  private IntVariable atomCounter;

  private GroundFormulas groundFormulas;

  private HashMap<FactorFormula, RelationExpression>
          groundingQueries = new HashMap<FactorFormula, RelationExpression>(),
          newQueries = new HashMap<FactorFormula, RelationExpression>();

  private RelationVariable clauses, newClauses, groundedClauses, newAtomCosts, atomCosts, trueAtoms, falseAtoms;

  boolean changed = false;

  private static Heading index_heading;
  private static Heading clause_heading;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();

  static {
    TypeBuilder typeBuilder = new TypeBuilder(TheBeast.getInstance().getNodServer());
    typeBuilder.doubleType().att("weight").
            boolType().arrayType().arrayType().att("signs").
            intType().arrayType().arrayType().att("atoms");
    clause_heading = typeBuilder.buildRelationType().heading();

    index_heading = typeBuilder.intType().att("index").relationType(1).buildRelationType().heading();
  }

  public WeightedSatProblem() {
  }

  /**
   * This returns a table that maps ground atoms to indices and a score.
   *
   * @param userPredicate the predicate we want the table for
   * @return a table with the following format: |arg1|arg2|...|argn|index|score|.
   */
  public RelationVariable getAtoms(UserPredicate userPredicate) {
    return atoms.get(userPredicate);
  }

  /**
   * This is the variable that represents the current count of atoms. It can be used
   *
   * @return a no d variable representing the current number of atoms.
   */
  IntVariable getAtomCounter() {
    return atomCounter;
  }


  public void init(Scores scores) {
    this.scores.load(scores);
  }

  public void solve(GroundAtoms solution) {
    updateSolver();
    boolean[] result = solver.solve();
  }

  public boolean isFractional() {
    return false;
  }

  public void update(GroundFormulas formulas, GroundAtoms atoms) {
    update(formulas, atoms, model.getFactorFormulas());
  }

  public void update(GroundFormulas formulas, GroundAtoms atoms, Collection<FactorFormula> factors) {
    int oldNumClauses = clauses.value().size();
    oldNumAtoms = atomCounter.value().getInt();
    for (FactorFormula factor : factors){
      interpreter.assign(groundedClauses, groundingQueries.get(factor));
      interpreter.assign(newClauses, newQueries.get(factor));
      interpreter.insert(clauses,newClauses);
    }
    for (UserPredicate pred : model.getHiddenPredicates()){
      interpreter.insert(this.atoms.get(pred),newAtoms.get(pred));
    }
    changed = clauses.value().size() > oldNumClauses || atomCounter.value().getInt() > oldNumAtoms;
  }

  public boolean changed() {
    return changed;
  }

  public void setClosure(GroundAtoms closure) {

  }

  public void enforceIntegerSolution() {

  }

  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;

    groundFormulas = new GroundFormulas(model, weights);
    scores = new Scores(model, weights);

    clauses = interpreter.createRelationVariable(clause_heading);
    trueAtoms = interpreter.createRelationVariable(index_heading);
    falseAtoms = interpreter.createRelationVariable(index_heading);
    atomCounter = interpreter.createIntVariable();
    atoms = new HashMap<UserPredicate, RelationVariable>();
    for (UserPredicate pred : model.getHiddenPredicates()) {
      RelationVariable variables = interpreter.createRelationVariable(pred.getHeadingArgsIndexScore());
      atoms.put(pred, variables);
      interpreter.addIndex(variables, "args", Index.Type.HASH, pred.getHeading().getAttributeNames());
      interpreter.addIndex(variables, "index", Index.Type.HASH, "index");
    }

    WeightedSatGrounder grounder = new WeightedSatGrounder();

    for (FactorFormula formula : model.getGlobalFactorFormulas()) {
      groundingQueries.put(formula, grounder.createGroundingQuery(formula, groundFormulas, this));
      newQueries.put(formula, builder.expr(clauses).expr(groundedClauses).relationMinus().getRelation());
    }
  }

  public void setProperty(PropertyName name, Object value) {

  }

  public Object getProperty(PropertyName name) {
    return null;
  }

  public void setProfiler(Profiler profiler) {

  }

  private static WeightedSatClause toClause(TupleValue tuple){
    ArrayValue signs = (ArrayValue) tuple.element("signs");
    boolean[][] signsArr = new boolean[signs.size()][];
    for (int i = 0; i < signsArr.length; ++i){
      ArrayValue disjunction = (ArrayValue) signs.element(i);
      signsArr[i] = new boolean[disjunction.size()];
      for (int j = 0; j < signsArr[i].length; ++j)
        signsArr[i][j] = disjunction.boolElement(j).getBool();
    }
    ArrayValue indices = (ArrayValue) tuple.element("signs");
    int[][] indicesArr = new int[signs.size()][];
    for (int i = 0; i < indicesArr.length; ++i){
      ArrayValue disjunction = (ArrayValue) indices.element(i);
      indicesArr[i] = new int[disjunction.size()];
      for (int j = 0; j < indicesArr[i].length; ++j)
        indicesArr[i][j] = disjunction.intElement(j).getInt();
    }
    double weight = tuple.doubleElement("weight").getDouble();
    return new WeightedSatClause(weight, indicesArr, signsArr);
  }

  private void updateSolver(){
    int howMany = atomCounter.value().getInt() - oldNumAtoms;
    double[] scores = newAtomCosts.getDoubleColumn("score");
    int[] indices = newAtomCosts.getIntColumn("index");
    double[] ordered = new double[scores.length];
    for (int i = 0; i < scores.length;++i)
      ordered[indices[i]] = scores[i];
    boolean[] states = new boolean[howMany];
    solver.addAtoms(states, ordered);

    WeightedSatClause[] clauses = new WeightedSatClause[newClauses.value().size()];
    int i = 0;
    for (TupleValue tuple : newClauses.value()){
      clauses[i++] = toClause(tuple);
    }
    solver.addClauses(clauses);
  }


  public Model getModel() {
    return model;
  }

  public WeightedSatSolver getSolver() {
    return solver;
  }

  public Scores getScores() {
    return scores;
  }

  public Weights getWeights() {
    return weights;
  }
}
