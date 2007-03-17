package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Insert;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.Value;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.solve.ILPSolver;
import thebeast.pml.solve.ILPSolverLpSolve;
import thebeast.pml.solve.ILPSolverOsi;
import thebeast.pml.solve.ILPSolverCbc;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class IntegerLinearProgram implements HasProperties {

  private RelationVariable
          constraints, newConstraints, vars, newVars, result;
  private HashMap<UserPredicate, RelationVariable>
          groundAtom2index = new HashMap<UserPredicate, RelationVariable>();
  private HashMap<FactorFormula, RelationVariable>
          groundFormula2index = new HashMap<FactorFormula, RelationVariable>();
  private HashMap<UserPredicate, RelationExpression>
          groundAtomGetWeight = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<FactorFormula, RelationExpression>
          groundFormulaGetWeight = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<FactorFormula, RelationExpression>
          formula2query = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<FactorFormula, Insert>
          newConstraintsInserts = new HashMap<FactorFormula, Insert>(),
          constraintsInserts = new HashMap<FactorFormula, Insert>();
  private HashMap<UserPredicate, RelationExpression>
          addTrueGroundAtoms = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          removeFalseGroundAtoms = new HashMap<UserPredicate, RelationExpression>();

  private IntVariable varCount;
  private IntVariable lastVarCount;
  private GroundFormulas formulas;
  private GroundAtoms solution, atoms, closure;
  private Scores scores;
  private Model model;
  private RelationVariable fractionals;
  private RelationExpression findFractionals;

  private boolean newFractionals;

  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private StatementFactory factory = TheBeast.getInstance().getNodServer().statementFactory();

  private Profiler profiler = new NullProfiler();

  private ILPSolver solver;


  private static Heading constraintHeading;
  private static Heading varHeading;
  private static Heading resultHeading;
  private static Heading valuesHeading;
  private static Heading indexHeading;
  private int newConstraintCount;


  static {
    LinkedList<Attribute> attributes = new LinkedList<Attribute>();
    TypeFactory typeFactory = TheBeast.getInstance().getNodServer().typeFactory();
    Attribute lb = typeFactory.createAttribute("lb", typeFactory.doubleType());
    Attribute ub = typeFactory.createAttribute("ub", typeFactory.doubleType());
    LinkedList<Attribute> values = new LinkedList<Attribute>();
    Attribute index = typeFactory.createAttribute("index", typeFactory.intType());
    Attribute weight = typeFactory.createAttribute("weight", typeFactory.doubleType());
    Attribute value = typeFactory.createAttribute("value", typeFactory.doubleType());
    values.add(index);
    values.add(weight);
    valuesHeading = typeFactory.createHeadingFromAttributes(values);
    Attribute valuesAttribute = typeFactory.createAttribute("values", typeFactory.createRelationType(valuesHeading));
    attributes.add(lb);
    attributes.add(ub);
    attributes.add(valuesAttribute);
    constraintHeading = typeFactory.createHeadingFromAttributes(attributes);

    LinkedList<Attribute> varAttributes = new LinkedList<Attribute>();
    varAttributes.add(index);
    varAttributes.add(weight);
    varHeading = typeFactory.createHeadingFromAttributes(varAttributes);

    LinkedList<Attribute> resultAttributes = new LinkedList<Attribute>();
    resultAttributes.add(index);
    resultAttributes.add(value);
    resultHeading = typeFactory.createHeadingFromAttributes(resultAttributes);

    indexHeading = typeFactory.createHeading(index);
  }


  public IntegerLinearProgram(Model model, Weights weights, ILPSolver solver) {
    this.solver = solver;
    this.model = model;
    varCount = interpreter.createIntVariable(builder.num(0).getInt());
    lastVarCount = interpreter.createIntVariable(builder.num(0).getInt());
    formulas = new GroundFormulas(model, weights);
    scores = new Scores(model, weights);
    constraints = interpreter.createRelationVariable(constraintHeading);
    newConstraints = interpreter.createRelationVariable(constraintHeading);
    result = interpreter.createRelationVariable(resultHeading);
    interpreter.addIndex(result, "index", Index.Type.HASH, "index");
    vars = interpreter.createRelationVariable(varHeading);
    interpreter.addIndex(vars, "index", Index.Type.HASH, "index");
    newVars = interpreter.createRelationVariable(varHeading);
    interpreter.addIndex(newVars, "index", Index.Type.HASH, "index");
    solution = model.getSignature().createGroundAtoms();
    atoms = model.getSignature().createGroundAtoms();
    closure = model.getSignature().createGroundAtoms();
    fractionals = interpreter.createRelationVariable(resultHeading);
    builder.expr(result);
    double eps = 1E-10;
    builder.doubleAttribute("value").num(0.0 + eps).doubleGreaterThan();
    builder.doubleAttribute("value").num(1.0 - eps).doubleLessThan().and(2).restrict();
    findFractionals = builder.getRelation();

    QueryGenerator generator = new QueryGenerator(weights, atoms);
    generator.setClosure(closure);
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationVariable variables = interpreter.createRelationVariable(predicate.getHeadingILP());
      interpreter.addIndex(variables, "args", Index.Type.HASH, predicate.getHeading().getAttributeNames());
      interpreter.addIndex(variables, "index", Index.Type.HASH, "index");
      groundAtom2index.put(predicate, variables);
      builder.clear();
      builder.expr(variables).from("c");
      builder.expr(lastVarCount).intAttribute("c", "index").intLEQ().where();
      builder.id("index").intAttribute("c", "index").id("weight").doubleAttribute("c", "score").tuple(2);
      builder.select().query();
      groundAtomGetWeight.put(predicate, builder.getRelation());

      builder.expr(result).from("result").expr(variables).from("vars");
      for (Attribute attribute : predicate.getHeading().attributes()) {
        builder.id(attribute.name()).attribute("vars", attribute);
      }
      builder.tupleForIds().select();
      builder.doubleAttribute("result", "value").num(0.5).doubleGEQ();
      builder.intAttribute("result", "index").intAttribute("vars", "index").equality();
      builder.and(2).where().query();
      addTrueGroundAtoms.put(predicate, builder.getRelation());

      builder.expr(solution.getGroundAtomsOf(predicate).getRelationVariable());
      builder.expr(result).from("result").expr(variables).from("vars");
      for (Attribute attribute : predicate.getHeading().attributes()) {
        builder.id(attribute.name()).attribute("vars", attribute);
      }
      builder.tupleForIds().select();
      builder.doubleAttribute("result", "value").num(0.5).doubleLEQ();
      builder.intAttribute("result", "index").intAttribute("vars", "index").equality();
      builder.and(2).where().query().relationMinus();
      removeFalseGroundAtoms.put(predicate, builder.getRelation());

    }
    for (FactorFormula formula : model.getFactorFormulas()) {
      if (!formula.isLocal()) {
        if (!formula.isAcyclicityConstraint() && !formula.isDeterministic()) {
          RelationVariable constraintVariables = interpreter.createRelationVariable(formula.getHeadingILP());
          groundFormula2index.put(formula, constraintVariables);
          RelationExpression query = generator.generateConstraintQuery(formula, formulas, scores, this, model);
          formula2query.put(formula, query);
          builder.expr(query).expr(constraints).relationMinus();
          Insert newConstraintsInsert = factory.createInsert(newConstraints, builder.getRelation());
          newConstraintsInserts.put(formula, newConstraintsInsert);
          Insert constraintsInsert = factory.createInsert(constraints, newConstraints);
          constraintsInserts.put(formula, constraintsInsert);

          builder.expr(constraintVariables).from("c");
          builder.expr(lastVarCount).intAttribute("c", "index").intLEQ().where();
          builder.id("index").intAttribute("c", "index").id("weight").doubleAttribute("c", "weight").tuple(2);
          builder.select().query();
          groundFormulaGetWeight.put(formula, builder.getRelation());

        } else {
          RelationExpression query = generator.generateConstraintQuery(formula, formulas, scores, this, model);
          formula2query.put(formula, query);
          builder.expr(query).expr(constraints).relationMinus();
          Insert newConstraintsInsert = factory.createInsert(newConstraints, builder.getRelation());
          newConstraintsInserts.put(formula, newConstraintsInsert);
          Insert constraintsInsert = factory.createInsert(constraints, newConstraints);
          constraintsInserts.put(formula, constraintsInsert);
        }
      }
    }


  }


  public void setClosure(GroundAtoms closure){
    this.closure.load(closure);
  }

  public RelationVariable getNewConstraints() {
    return newConstraints;
  }

  public RelationVariable getConstraints() {
    return constraints;
  }


  public Profiler getProfiler() {
    return profiler;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
  }

  public void build(GroundFormulas formulas, GroundAtoms atoms, Scores scores) {
    solver.init();
    interpreter.assign(varCount, builder.num(0).getInt());
    this.formulas.load(formulas);
    this.scores.load(scores);
    this.atoms.load(atoms);
    for (FactorFormula formula : formula2query.keySet()) {
      interpreter.interpret(newConstraintsInserts.get(formula));
      interpreter.interpret(constraintsInserts.get(formula));
//      interpreter.insert(newConstraints, formula2query.get(formula));
//      interpreter.insert(constraints, newConstraints);
    }
    interpreter.clear(newVars);
    //get the new variables
    for (FactorFormula formula : groundFormula2index.keySet()) {
      interpreter.insert(newVars, groundFormulaGetWeight.get(formula));
    }
    for (UserPredicate predicate : groundAtom2index.keySet()) {
      interpreter.insert(newVars, groundAtomGetWeight.get(predicate));
    }
    interpreter.insert(vars, newVars);
  }

  public void init(Scores scores) {
    this.scores.load(scores);
    solver.init();
    clear();
  }

  public int getNumRows(){
    return constraints.value().size();
  }

  private void clear() {
    interpreter.clear(constraints);
    interpreter.clear(newConstraints);
    interpreter.clear(vars);
    interpreter.clear(newVars);
    interpreter.clear(fractionals);
    interpreter.assign(varCount, builder.num(0).getInt());
    for (RelationVariable var : groundAtom2index.values())
      interpreter.clear(var);
    for (RelationVariable var : groundFormula2index.values())
      interpreter.clear(var);
  }

  public void setSolver(ILPSolver solver) {
    this.solver = solver;
    clear();
  }


  public ILPSolver getSolver() {
    return solver;
  }

  public boolean changed() {
    //return newConstraintCount > 0 || newFractionals;
    return newConstraints.value().size() > 0 || newFractionals;
  }


  public RelationVariable getNewVars() {
    return newVars;
  }

  public RelationVariable getVars() {
    return vars;
  }

  public void solve(GroundAtoms solution) {
    profiler.start("add to ilp", 0);
    solver.add(newVars, newConstraints);
    profiler.end();
    profiler.start("solve", 1);
    RelationVariable result = solver.solve();
    profiler.end();
    profiler.start("extract", 2);
    interpreter.assign(this.result, result);
    extractSolution(solution);
    profiler.end();
    newFractionals = false;
    findFractionals();
  }

  private void findFractionals() {
    interpreter.assign(fractionals, findFractionals);
  }

  public RelationVariable getFractionals() {
    return fractionals;
  }

  public boolean isFractional() {
    return fractionals.value().size() > 0;
  }

  private void extractSolution(GroundAtoms solution) {
    this.solution.load(solution);
    //for all solutions <= 0.5 remove tuples
    //System.out.println(result.value());
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      //System.out.println(solution.getGroundAtomsOf(predicate).getRelationVariable());
      interpreter.assign(solution.getGroundAtomsOf(predicate).getRelationVariable(), removeFalseGroundAtoms.get(predicate));
      //System.out.println(solution.getGroundAtomsOf(predicate).getRelationVariable());
    }

    //for all solutions > 0.5 insert tuples (taken from the corresponding ground atom index table)
    //into the ground atoms
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationExpression query = addTrueGroundAtoms.get(predicate);
      //RelationValue rel = interpreter.evaluateRelation(query);
      interpreter.insert(solution.getGroundAtomsOf(predicate).getRelationVariable(), query);
    }

  }

  public void update(GroundFormulas formulas, GroundAtoms atoms) {
    update(formulas,atoms, formula2query.keySet());
  }
  
  public void update(GroundFormulas formulas, GroundAtoms atoms, Collection<FactorFormula> factors){


    interpreter.assign(lastVarCount, varCount);
    this.formulas.load(formulas);
    //this.scores.load(scores);
    this.atoms.load(atoms);
    newConstraintCount = 0;
//    System.out.println(atoms);
//    System.out.println(formulas);
    interpreter.clear(newConstraints);
    profiler.start("find new constraints");
    for (FactorFormula formula : factors) {
      //System.out.println(formula);
      profiler.start(formula.toString().substring(formula.toString().length()-20));
      interpreter.interpret(newConstraintsInserts.get(formula));
      newConstraintCount += newConstraints.value().size();
      profiler.end();
      //interpreter.interpret(constraintsInserts.get(formula));
//      System.out.println(formula);
//      System.out.println(toLpSolveFormat(newVars, newConstraints));
//      System.out.println(toLpSolveFormat(vars, constraints));

//      RelationExpression query = formula2query.get(formula);
//      builder.expr(query);
//      builder.expr(constraints);
//      interpreter.insert(newConstraints, builder.relationMinus().getRelation());
//      interpreter.insert(constraints, newConstraints);
    }
    //System.out.println("Updating ...");
    profiler.end();
    profiler.start("insert constraints");
    for (FactorFormula formula : factors){
      //System.out.println(formula);
      //System.out.println("Before insertion");
      //System.out.println(toLpSolveFormat(newVars, constraints));
      interpreter.interpret(constraintsInserts.get(formula));
      //System.out.println("After insertion");
      //System.out.println(toLpSolveFormat(newVars, constraints));
    }
    profiler.end();    
    //System.out.println(constraints.value());
    interpreter.clear(newVars);
    //get the new variables
    for (FactorFormula formula : groundFormula2index.keySet()) {
      interpreter.insert(newVars, groundFormulaGetWeight.get(formula));
    }
    for (UserPredicate predicate : groundAtom2index.keySet()) {
      interpreter.insert(newVars, groundAtomGetWeight.get(predicate));
    }
    interpreter.insert(vars, newVars);

  }

  public RelationVariable getGroundAtomIndices(UserPredicate predicate) {
    return groundAtom2index.get(predicate);
  }


  public IntVariable getVarCount() {
    return varCount;
  }

  public RelationVariable getGroundFormulaIndices(FactorFormula formula) {
    return groundFormula2index.get(formula);
  }

  public static Heading getConstraintHeading() {
    return constraintHeading;
  }

  public static Heading getResultHeading() {
    return resultHeading;
  }

  public static Heading getVarHeading() {
    return varHeading;
  }


  public static Heading getValuesHeading() {
    return valuesHeading;
  }


  public String toString() {
    StringBuffer buffer = new StringBuffer();
    for (UserPredicate pred : groundAtom2index.keySet())
      buffer.append(pred.getName()).append(":\n").append(groundAtom2index.get(pred).value());

    buffer.append("Variables:\n");
    buffer.append(vars.value());
    buffer.append("Constraints:\n");
    buffer.append(constraints.value());

    return buffer.toString();
  }


  /**
   * Returns a simple column-based representation of the current result for this ILP
   *
   * @return a string containing two columns: the variable name (predicate+args) and the value.
   */
  public String getResultString() {
    return getVariableString(this.result.value());
  }

  public String getVariableString(RelationValue value) {
    StringBuffer result = new StringBuffer();
    if (value.size() == 0) return "No Fractionals.";
    for (TupleValue var : value) {
      Formatter formatter = new Formatter();
      result.append(formatter.format("%-12s\t", indexToVariableString(var.intElement("index").getInt())));
      result.append(var.doubleElement("value")).append("\n");
    }
    return result.toString();
  }


  /**
   * Returns a representation of this ILP in LpSolve ("lp") format. This method should mostly be called for debugging
   * purposes (it is not very optimized).
   *
   * @return ilp in lpsolve format.
   */
  public String toLpSolveFormat() {
    return toLpSolveFormat(vars, constraints);
  }


  /**
   * Calling this method will guarantee that when {@link thebeast.pml.IntegerLinearProgram#solve(GroundAtoms)} is called
   * the next time the solution will be integer.
   */
  public void enforceIntegerSolution() {
    if (isFractional()) {
      solver.addIntegerConstraints(fractionals);
      newFractionals = true;
    }
  }

  /**
   * Returns a string for introspection of this ilp.
   *
   * @param vars        the variables to print out (a relation)
   * @param constraints the constraints to print out (a relation).
   * @return ilp in verbose format
   */
  public String toVerboseFormat(RelationVariable vars, RelationVariable constraints) {
    StringBuffer result = new StringBuffer();

    result.append("max: ");
    int index = 0;
    for (TupleValue var : vars.value()) {
      double weight = var.doubleElement("weight").getDouble();
      if (weight != 0.0) {
        if (index++ > 0) result.append(" + ");
        result.append(weight).append(" ");
        result.append(indexToVariableString(var.intElement("index").getInt())).append("\n");
      }
    }

    result.append(";\n\n");
    for (TupleValue tuple : constraints.value()) {
      double lb = tuple.doubleElement("lb").getDouble();
      double ub = tuple.doubleElement("ub").getDouble();
      index = 0;
      for (TupleValue value : tuple.relationElement("values")) {
        if (index++ > 0) result.append(" + ");
        result.append(value.doubleElement("weight")).append(" ");
        result.append(indexToVariableString(value.intElement("index").getInt()));
      }
      if (lb == Double.NEGATIVE_INFINITY) {
        result.append(" <= ").append(ub).append(";\n");
      } else if (ub == Double.POSITIVE_INFINITY) {
        result.append(" >= ").append(lb).append(";\n");
      } else if (ub == lb) {
        result.append(" = ").append(lb).append(";\n");
      }
    }
    return result.toString();
  }


  /**
   * Returns a representation of this ILP in LpSolve ("lp") format. This method should mostly be called for debugging
   * purposes (it is not very optimized).
   *
   * @param vars        the variables to print out (a relation)
   * @param constraints the constraints to print out (a relation).
   * @return ilp in lpsolve format.
   */
  public String toLpSolveFormat(RelationVariable vars, RelationVariable constraints) {
    StringBuffer result = new StringBuffer();

    result.append("max: ");
    int index = 0;
    for (TupleValue var : vars.value()) {
      double weight = var.doubleElement("weight").getDouble();
      if (weight != 0.0) {
        if (index++ > 0) result.append(" + ");
        result.append(weight).append(" ");
        result.append(indexToVariableString(var.intElement("index").getInt())).append("\n");
      }
    }

    result.append(";\n\n");
    for (TupleValue tuple : constraints.value()) {
      double lb = tuple.doubleElement("lb").getDouble();
      double ub = tuple.doubleElement("ub").getDouble();
      index = 0;
      for (TupleValue value : tuple.relationElement("values")) {
        if (index++ > 0) result.append(" + ");
        result.append(value.doubleElement("weight")).append(" ");
        result.append(indexToVariableString(value.intElement("index").getInt()));
      }
      if (lb == Double.NEGATIVE_INFINITY) {
        result.append(" <= ").append(ub).append(";\n");
      } else if (ub == Double.POSITIVE_INFINITY) {
        result.append(" >= ").append(lb).append(";\n");
      } else if (ub == lb) {
        result.append(" = ").append(lb).append(";\n");
      }
    }
    return result.toString();
  }

  /**
   * Returns a string representation of the variable with the given index.
   *
   * @param index the index of the variable.
   * @return a string representation with predicate name and arguments.
   */
  public String indexToVariableString(int index) {
    for (Map.Entry<UserPredicate, RelationVariable> entry : groundAtom2index.entrySet()) {
      builder.expr(entry.getValue()).intAttribute("index").num(index).equality().restrict();
      RelationValue result = interpreter.evaluateRelation(builder.getRelation());
      if (result.size() == 1) {
        StringBuffer buffer = new StringBuffer(entry.getKey().getName());
        int argIndex = 0;
        for (Value value : result.iterator().next().values())
          if (argIndex++ < entry.getKey().getArity())
            buffer.append("_").append(value.toString());
          else
            break;
        return buffer.toString();
      }
    }
    for (Map.Entry<FactorFormula, RelationVariable> entry : groundFormula2index.entrySet()) {
      builder.expr(entry.getValue()).intAttribute("index").num(index).equality().restrict();
      RelationValue result = interpreter.evaluateRelation(builder.getRelation());
      if (result.size() == 1) {
        StringBuffer buffer = new StringBuffer(entry.getKey().getName());
        TupleValue tuple = result.iterator().next();
        for (int i = 1; i < tuple.size() - 1; ++i)
          buffer.append("_").append(tuple.element(i).toString());
        return buffer.toString();
      }
    }
    return "NOT AVAILABLE";
  }

  /**
   * Returns a string representation of the variable with the given index.
   *
   * @param index the index of the variable.
   * @return a string representation with predicate name and arguments.
   */
  public String indexToPredicateString(int index) {
    for (Map.Entry<UserPredicate, RelationVariable> entry : groundAtom2index.entrySet()) {
      builder.expr(entry.getValue()).intAttribute("index").num(index).equality().restrict();
      RelationValue result = interpreter.evaluateRelation(builder.getRelation());
      if (result.size() == 1) {
        StringBuffer buffer = new StringBuffer(entry.getKey().getName());
        int argIndex = 0;
        buffer.append("(");
        for (Value value : result.iterator().next().values()) {
          if (argIndex > 0 && argIndex < entry.getKey().getArity()) buffer.append(",");
          if (argIndex++ < entry.getKey().getArity())
            buffer.append(value.toString());
          else
            break;
        }
        buffer.append(")");
        return buffer.toString();
      }
    }
    for (Map.Entry<FactorFormula, RelationVariable> entry : groundFormula2index.entrySet()) {
      if (entry.getKey().isParametrized()) {
        //builder.expr(scores.getWeights().getRelation(entry.getKey().getWeightFunction())).from;
        builder.expr(entry.getValue()).intAttribute("index").num(index).equality().restrict();
        RelationValue result = interpreter.evaluateRelation(builder.getRelation());
        if (result.size() == 1) {
          return entry.getKey().getWeightFunction().getName();

        }
      }
    }
    return "NOT AVAILABLE";
  }


  public void setProperty(PropertyName name, Object value) {
    if ("solver".equals(name.getHead()))
      if (name.isTerminal()) {
        String type = (String) value;
        if ("lpsolve".equals(type))
          solver = new ILPSolverLpSolve();
        else if ("osi".equals(type))
          solver = new ILPSolverOsi();
        else if ("cbc".equals(type))
          solver = new ILPSolverCbc();
        else
          throw new IllegalPropertyValueException(name, value);
      } else
        solver.setProperty(name.getTail(), value);
  }

  public Object getProperty(PropertyName name) {
    if ("constraints".equals(name.getHead())) {
      UserPredicate predicate = model.getSignature().getUserPredicate(name.getTail().getHead());
      return allConstraintsFor(predicate, name.getTail().getArguments().toArray());
    }
    if ("result".equals(name.getHead()))
      return getResultString();
    if ("solver".equals(name.getHead()))
      return solver;
    if ("fractionals".equals(name.getHead()))
      return getVariableString(fractionals.value());
    return null;
  }

  public int getVariableIndex(UserPredicate predicate, Object... args) {
    builder.expr(groundAtom2index.get(predicate));
    for (int i = 0; i < predicate.getArity(); ++i) {
      builder.id(predicate.getColumnName(i)).value(predicate.getHeading().attributes().get(i).type(), args[i]);
    }
    builder.tupleForIds().id("index").num(-1).id("score").num(0.0).tupleForIds().get().intExtractComponent("index");
    return interpreter.evaluateInt(builder.getInt()).getInt();
  }

  public String allConstraintsFor(UserPredicate predicate, Object... args) {
    StringBuffer result = new StringBuffer();
    int varIndex = getVariableIndex(predicate, args);
    for (TupleValue constraint : constraints.value()) {
      StringBuffer constraintBuffer = new StringBuffer();
      boolean hasVariable = false;
      int elementNr = 0;
      for (TupleValue element : constraint.relationElement("values")) {
        if (elementNr++ > 0) constraintBuffer.append(" + ");
        int index = element.intElement("index").getInt();
        if (index == varIndex) {
          hasVariable = true;
        }
        constraintBuffer.append(element.doubleElement("weight")).append(" ");
        constraintBuffer.append(indexToPredicateString(index));
      }
      double lb = constraint.doubleElement("lb").getDouble();
      double ub = constraint.doubleElement("ub").getDouble();
      if (lb == Double.NEGATIVE_INFINITY) {
        constraintBuffer.append(" <= ").append(ub).append(";\n");
      } else if (ub == Double.POSITIVE_INFINITY) {
        constraintBuffer.append(" >= ").append(lb).append(";\n");
      } else if (ub == lb) {
        constraintBuffer.append(" = ").append(lb).append(";\n");
      }
      if (hasVariable) result.append(constraintBuffer);
    }
    return result.toString();
  }

}
