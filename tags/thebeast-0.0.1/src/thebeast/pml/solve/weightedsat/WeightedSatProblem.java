package thebeast.pml.solve.weightedsat;

import thebeast.pml.*;
import thebeast.pml.solve.PropositionalModel;
import thebeast.pml.formula.FactorFormula;
import thebeast.util.Profiler;
import thebeast.util.NullProfiler;
import thebeast.util.HashMultiMapList;
import thebeast.util.Util;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.Heading;
import thebeast.nod.type.Attribute;
import thebeast.nod.util.TypeBuilder;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.ArrayValue;
import thebeast.nod.value.RelationValue;

import java.util.*;

/**
 * A WeightedSatProblem represents a set of weighted clauses in CNF. For effiency it stores clauses
 * in database tables.
 *
 * @author Sebastian Riedel
 */
public class WeightedSatProblem implements PropositionalModel {

  private Model model;
  private WeightedSatSolver solver;
  private Scores scores;
  private Weights weights;
  private Profiler profiler = new NullProfiler();
  private double epsilon = 0.0;
  private boolean singleCallMode;

  private int oldNumAtoms;

  private HashMap<UserPredicate, RelationVariable>
          mappings = new HashMap<UserPredicate, RelationVariable>(),
          newMappings = new HashMap<UserPredicate, RelationVariable>();
  private IntVariable atomCounter;

  private GroundFormulas groundFormulas;
  private GroundAtoms solution;
  private GroundAtoms lastSolution;
  private GroundAtoms atoms;

  private HashMap<FactorFormula, RelationExpression>
          groundingQueries = new HashMap<FactorFormula, RelationExpression>(),
          newQueries = new HashMap<FactorFormula, RelationExpression>();

  private HashMap<UserPredicate, RelationExpression>
          getTrueIndices = new HashMap<UserPredicate, RelationExpression>(),
          groundObjective = new HashMap<UserPredicate, RelationExpression>(),
          scoresAndIndices = new HashMap<UserPredicate, RelationExpression>(),
          removeFalseAtoms = new HashMap<UserPredicate, RelationExpression>(),
          addTrueAtoms = new HashMap<UserPredicate, RelationExpression>();

  private RelationExpression newAtomCostsQuery, newClausesQuery;

  private RelationVariable
          clauses, newClauses, groundedClauses,
          newAtomCosts, oldAtomCosts, atomCosts, trueAtoms, falseAtoms, lastTrueIndices;

  boolean changed = false;

  private static Heading index_heading;
  private static Heading clause_heading;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
  private static Heading indexScore_heading;
  private boolean buildLocalModel = false;
  private WeightedSatGrounder grounder;

  static {
    TypeBuilder typeBuilder = new TypeBuilder(TheBeast.getInstance().getNodServer());
    typeBuilder.doubleType().att("weight").
            boolType().arrayType().arrayType().att("signs").
            intType().arrayType().arrayType().att("atoms").
            intType().att("ub").intType().att("lb").intType().att("disjunction").intType().att("index").
            relationType(1).att("items").relationType(4).att("constraints").
            relationType(4);
    clause_heading = typeBuilder.buildRelationType().heading();

    index_heading = typeBuilder.intType().att("index").relationType(1).buildRelationType().heading();

    indexScore_heading = typeBuilder.intType().att("index").doubleType().att("score").
            relationType(2).buildRelationType().heading();

  }


  public WeightedSatProblem(WeightedSatSolver solver) {
    this.solver = solver;
    grounder = new WeightedSatGrounder();
  }

  /**
   * This returns a table that maps ground atoms to indices and a score.
   *
   * @param userPredicate the predicate we want the table for
   * @return a table with the following format: |arg1|arg2|...|argn|index|score|.
   */
  public RelationVariable getMapping(UserPredicate userPredicate) {
    return mappings.get(userPredicate);
  }

  public RelationVariable getNewMapping(UserPredicate pred) {
    return newMappings.get(pred);
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
    solver.init();
    clear();
  }

  public void buildLocalModel() {
    oldNumAtoms = atomCounter.value().getInt();
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationVariable target = mappings.get(predicate);
      interpreter.assign(target, groundObjective.get(predicate));
      //interpreter.insert(atomCosts, scoresAndIndices.get(predicate));
    }
    //interpreter.assign(newAtomCosts, newAtomCostsQuery);
    buildLocalModel = true;

  }

  public void solve(GroundAtoms solution) {
    this.solution.load(solution);
    profiler.start("updatesolver");
    updateSolver();
    profiler.end().start("solve");
    boolean[] result = solver.solve();
    profiler.end().start("filltables");
    //System.out.println(Arrays.toString(result));
    fillTrueFalseTables(result);
    profiler.end().start("extractresult");
    //System.out.println(trueAtoms.value());
    //System.out.println(falseAtoms.value());
    for (UserPredicate pred : model.getHiddenPredicates()) {
      RelationVariable target = solution.getGroundAtomsOf(pred).getRelationVariable();
      interpreter.assign(target, removeFalseAtoms.get(pred));
      interpreter.insert(target, addTrueAtoms.get(pred));
    }
    profiler.end();
    //System.out.println(solution);

  }

  public boolean isFractional() {
    return false;
  }

  public void update(GroundFormulas formulas, GroundAtoms atoms) {
    update(formulas, atoms, model.getGlobalFactorFormulas());
    //System.out.println(formulas);
    //System.out.println(toString());

  }

  private void clear() {
    interpreter.assign(atomCounter, builder.num(0).getInt());
    interpreter.clear(clauses);
    interpreter.clear(newClauses);
    interpreter.clear(groundedClauses);
    interpreter.clear(oldAtomCosts);
    interpreter.clear(atomCosts);
    for (UserPredicate pred : model.getHiddenPredicates()) {
      //interpreter.insert(this.mappings.get(pred), newMappings.get(pred));
      interpreter.clear(mappings.get(pred));
    }


  }

  public void update(GroundFormulas formulas, GroundAtoms atoms, Collection<FactorFormula> factors) {
    lastSolution.load(atoms, model.getHiddenPredicates());
    this.atoms.load(atoms);
    int oldNumClauses = clauses.value().size();
    if (!buildLocalModel)
      oldNumAtoms = atomCounter.value().getInt();
    else
      buildLocalModel = false;
    groundFormulas.load(formulas);
    interpreter.clear(newClauses);
    interpreter.clear(groundedClauses);
    if (!singleCallMode) {
      for (FactorFormula factor : factors) {
        interpreter.append(groundedClauses, groundingQueries.get(factor));
      }
      interpreter.assign(newClauses, newClausesQuery);
      //interpreter.
//    System.out.println("newClauses.byteSize() = " + newClauses.byteSize());
      interpreter.append(clauses, newClauses);
//    System.out.println("clauses.byteSize() = " + clauses.byteSize());
      interpreter.assign(oldAtomCosts, atomCosts);
      interpreter.clear(atomCosts);
      for (UserPredicate pred : model.getHiddenPredicates()) {
        interpreter.insert(atomCosts, scoresAndIndices.get(pred));
      }
      interpreter.assign(newAtomCosts, newAtomCostsQuery);
    } else {
      //System.out.println("Single-Call-Mode");
      for (FactorFormula factor : factors) {
        interpreter.append(newClauses, groundingQueries.get(factor));
      }
      //interpreter.assign(newClauses, newClausesQuery);
      //interpreter.
//    System.out.println("newClauses.byteSize() = " + newClauses.byteSize());
      //interpreter.assign(clauses, newClauses);
//    System.out.println("clauses.byteSize() = " + clauses.byteSize());
      interpreter.assign(oldAtomCosts, atomCosts);
      interpreter.clear(atomCosts);
      for (UserPredicate pred : model.getHiddenPredicates()) {
        interpreter.append(atomCosts, scoresAndIndices.get(pred));
      }
      interpreter.assign(newAtomCosts, atomCosts);
    }
    if (epsilon == 0.0)
      changed = clauses.value().size() > oldNumClauses || atomCounter.value().getInt() > oldNumAtoms;
    else {
      double bound = 0;
      double[] scores = newAtomCosts.getDoubleColumn("score");
      for (double score : scores) bound += Math.abs(score);
      scores = newClauses.getDoubleColumn("score");
      for (double score : scores) bound += Math.abs(score);
      changed = bound < epsilon;
    }
  }

  public boolean changed() {
    return changed;
  }

  public void setClosure(GroundAtoms closure) {

  }

  private void fillTrueFalseTables(boolean[] result) {
    int trueCount = 0;
    for (boolean state : result) if (state) ++trueCount;
    int falseCount = result.length - trueCount;
    int[] trueIndices = new int[trueCount];
    int[] falseIndices = new int[falseCount];
    int truePointer = 0;
    int falsePointer = 0;
    for (int i = 0; i < result.length; ++i)
      if (result[i]) trueIndices[truePointer++] = i;
      else falseIndices[falsePointer++] = i;
    trueAtoms.assignByArray(trueIndices, null);
    falseAtoms.assignByArray(falseIndices, null);

  }


  public void enforceIntegerSolution() {

  }

  public void setFullyGround(FactorFormula formula, boolean fullyGround) {
    groundingQueries.put(formula, grounder.createGroundingQuery(formula, groundFormulas, atoms, fullyGround, weights, this));
  }

  public int getGroundAtomCount() {
    return atomCosts.value().size();
  }

  public int getGroundFormulaCount() {
    return clauses.value().size();
  }

  public String getPropertyString() {
    return null;
  }

  public void configure(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;

    groundFormulas = new GroundFormulas(model, weights);
    solution = model.getSignature().createGroundAtoms();
    atoms = model.getSignature().createGroundAtoms();
    lastSolution = model.getSignature().createGroundAtoms();
    scores = new Scores(model, weights);

    clauses = interpreter.createRelationVariable(clause_heading);
    newClauses = interpreter.createRelationVariable(clause_heading);
    groundedClauses = interpreter.createRelationVariable(clause_heading);
    trueAtoms = interpreter.createRelationVariable(index_heading);
    interpreter.addIndex(trueAtoms, "index", Index.Type.HASH, "index");
    falseAtoms = interpreter.createRelationVariable(index_heading);
    interpreter.addIndex(falseAtoms, "index", Index.Type.HASH, "index");
    atomCounter = interpreter.createIntVariable();
    atomCosts = interpreter.createRelationVariable(indexScore_heading);
    interpreter.addIndex(atomCosts, "index", Index.Type.HASH, "index");
    oldAtomCosts = interpreter.createRelationVariable(indexScore_heading);
    interpreter.addIndex(oldAtomCosts, "index", Index.Type.HASH, "index");
    newAtomCosts = interpreter.createRelationVariable(indexScore_heading);
    interpreter.addIndex(newAtomCosts, "index", Index.Type.HASH, "index");
    mappings = new HashMap<UserPredicate, RelationVariable>();

    clauses = interpreter.createRelationVariable(clause_heading);
    groundedClauses = interpreter.createRelationVariable(clause_heading);

    lastTrueIndices = interpreter.createRelationVariable(index_heading);

    for (UserPredicate pred : model.getHiddenPredicates()) {
      RelationVariable mapping = interpreter.createRelationVariable(pred.getHeadingArgsIndexScore());
      mappings.put(pred, mapping);
      interpreter.addIndex(mapping, "args", Index.Type.HASH, pred.getHeading().getAttributeNames());
      interpreter.addIndex(mapping, "index", Index.Type.HASH, "index");
      //create query to update solutions
      //remove wrong solutions
      builder.expr(solution.getGroundAtomsOf(pred).getRelationVariable());
      builder.expr(mapping).from("mapping").expr(falseAtoms).from("falseAtoms");
      builder.intAttribute("mapping", "index").intAttribute("falseAtoms", "index").equality().where();
      for (int i = 0; i < pred.getArity(); ++i) {
        builder.id(pred.getColumnName(i)).attribute("mapping", pred.getAttribute(i));
      }
      builder.tuple(pred.getArity()).select().query();
      builder.relationMinus();
      removeFalseAtoms.put(pred, builder.getRelation());

      //a query that produces a table with atoms to add
      builder.expr(mapping).from("mapping").expr(trueAtoms).from("trueAtoms");
      builder.intAttribute("mapping", "index").intAttribute("trueAtoms", "index").equality().where();
      for (int i = 0; i < pred.getArity(); ++i) {
        builder.id(pred.getColumnName(i)).attribute("mapping", pred.getAttribute(i));
      }
      builder.tuple(pred.getArity()).select().query();
      addTrueAtoms.put(pred, builder.getRelation());

      //a query that takes a solution (ground atoms) and creates a list of corresponding indices
      builder.expr(mapping).from("mapping").
              expr(lastSolution.getGroundAtomsOf(pred).getRelationVariable()).from("last");
      for (int i = 0; i < pred.getArity(); ++i) {
        builder.attribute("mapping", pred.getAttribute(i)).attribute("last", pred.getAttribute(i)).equality();
      }
      builder.and(pred.getArity()).where();
      builder.id("index").intAttribute("mapping", "index").tuple(1).select().query();
      getTrueIndices.put(pred, builder.getRelation());

      //a query that selects the scores and indices from the mapping
      builder.expr(mapping).from("mapping").
              id("index").intAttribute("mapping", "index").
              id("score").doubleAttribute("mapping", "score").tupleForIds().select().query();
      scoresAndIndices.put(pred, builder.getRelation());

      builder.expr(scores.getScoreRelation(pred)).from("scores");
      for (Attribute attribute : pred.getHeading().attributes()) {
        builder.id(attribute.name()).attribute("scores", attribute);
      }
      builder.id("index").expr(atomCounter).intPostInc();
      builder.id("score").doubleAttribute("scores", "score");
      builder.tupleForIds().select().query();
      groundObjective.put(pred, builder.getRelation());


    }

    builder.expr(atomCosts).expr(oldAtomCosts).relationMinus();
    newAtomCostsQuery = builder.getRelation();


    for (FactorFormula formula : model.getGlobalFactorFormulas()) {
      groundingQueries.put(formula, grounder.createGroundingQuery(formula, groundFormulas, atoms,false, weights, this));
      //newQueries.put(formula, builder.expr(groundedClauses).expr(clauses).relationMinus().getRelation());
    }
    newClausesQuery = builder.expr(groundedClauses).expr(clauses).relationMinus().getRelation();
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("solver")) {
      if (name.isTerminal()) {
        if ("maxwalksat".equals(value))
          solver = new MaxWalkSat();
        else if ("maxproduct".equals(value))
          solver = new MaxProduct();
      } else
        solver.setProperty(name.getTail(), value);
    } else if (name.getHead().equals("singleCallMode")) {
      setSingleCallMode((Boolean) value);
    } else if (name.getHead().equals("detWeight")) {
      grounder.setDetWeight((Double)value);
    }
  }

  public Object getProperty(PropertyName name) {
    return null;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
    if (solver != null) solver.setProfiler(profiler);
  }

  private static WeightedSatClause toClause(TupleValue tuple) {
    ArrayValue signs = (ArrayValue) tuple.element("signs");
    boolean[][] signsArr = new boolean[signs.size()][];
    for (int i = 0; i < signsArr.length; ++i) {
      ArrayValue disjunction = (ArrayValue) signs.element(i);
      signsArr[i] = new boolean[disjunction.size()];
      for (int j = 0; j < signsArr[i].length; ++j)
        signsArr[i][j] = disjunction.boolElement(j).getBool();
    }
    ArrayValue indices = (ArrayValue) tuple.element("atoms");
    int[][] indicesArr = new int[signs.size()][];
    for (int i = 0; i < indicesArr.length; ++i) {
      ArrayValue disjunction = (ArrayValue) indices.element(i);
      indicesArr[i] = new int[disjunction.size()];
      for (int j = 0; j < indicesArr[i].length; ++j)
        indicesArr[i][j] = disjunction.intElement(j).getInt();
    }
    double weight = tuple.doubleElement("weight").getDouble();
    WeightedSatClause.Constraint[][] constraints = new WeightedSatClause.Constraint[indices.size()][];
    RelationValue cardConstraints = tuple.relationElement("constraints");
    HashMultiMapList<Integer, WeightedSatClause.Constraint> disjunction2Constraints =
            new HashMultiMapList<Integer, WeightedSatClause.Constraint>();
    for (TupleValue c : cardConstraints) {
      RelationValue items = c.relationElement("items");
      int[] itemIndices = new int[items.size()];
      int itemIndex = 0;
      for (TupleValue item : items) {
        itemIndices[itemIndex++] = item.intElement(0).getInt();
      }
      int lb = c.intElement("lb").getInt();
      int ub = c.intElement("ub").getInt();
      int disjunctionIndex = c.intElement("disjunction").getInt();
      disjunction2Constraints.add(disjunctionIndex, new WeightedSatClause.Constraint(lb, ub, itemIndices));
    }
    for (Map.Entry<Integer, List<WeightedSatClause.Constraint>> entry : disjunction2Constraints.entrySet()) {
      constraints[entry.getKey()] = entry.getValue().toArray(new WeightedSatClause.Constraint[0]);
    }

    return new WeightedSatClause(weight, indicesArr, signsArr, constraints);
  }

  private void updateSolver() {
    profiler.start("prepare-update");
    interpreter.clear(lastTrueIndices);
    for (UserPredicate pred : model.getHiddenPredicates()) {
      interpreter.append(lastTrueIndices, getTrueIndices.get(pred));
    }
    int[] trueIndices = lastTrueIndices.getIntColumn("index");

    int howMany = atomCounter.value().getInt() - oldNumAtoms;
    double[] scores = newAtomCosts.getDoubleColumn("score");
    int[] indices = newAtomCosts.getIntColumn("index");
    double[] ordered = new double[scores.length];
    for (int i = 0; i < scores.length; ++i)
      ordered[indices[i] - oldNumAtoms] = scores[i];
    //boolean[] states = new boolean[howMany];
    boolean[] states = new boolean[atomCounter.value().getInt()];
    for (int trueIndex : trueIndices)
      states[trueIndex] = true;
    //Arrays.fill(states, true);
    solver.addAtoms(ordered);
    solver.setStates(states);

    //System.err.println("Before transformation we use " + Util.toMemoryString(Runtime.getRuntime().totalMemory()));    
    WeightedSatClause[] clauses = new WeightedSatClause[newClauses.value().size()];
    int i = 0;
    for (TupleValue tuple : newClauses.value()) {
      clauses[i++] = toClause(tuple);
    }
    profiler.end().start("transfer");
    if (singleCallMode){
      interpreter.clear(newClauses);
      interpreter.compactify(newClauses);
    }
    //System.err.println("transferring " + clauses.length + " clauses to solver");
    //System.err.println("Using " + Util.toMemoryString(Runtime.getRuntime().totalMemory()));
    solver.addClauses(clauses);
    profiler.end();
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

  public String toString() {
    StringBuffer result = new StringBuffer();
    //result.append(groundedClauses.value());
    //result.append(newClauses.value());
    result.append(clauses.value());
    result.append(atomCosts.value());
    for (UserPredicate pred : model.getHiddenPredicates()) {
      result.append(mappings.get(pred).value());
    }
    return result.toString();
  }


  public boolean isSingleCallMode() {
    return singleCallMode;
  }

  public void setSingleCallMode(boolean singleCallMode) {
    this.singleCallMode = singleCallMode;
  }
}
