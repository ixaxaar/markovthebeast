package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Index;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class IntegerLinearProgram {

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
  private HashMap<UserPredicate, RelationExpression>
          addTrueGroundAtoms = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          removeFalseGroundAtoms = new HashMap<UserPredicate, RelationExpression>();

  private IntVariable varCount;
  private IntVariable lastVarCount;
  private GroundFormulas formulas;
  private GroundAtoms solution, atoms;
  private Scores scores;
  private Model model;

  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

  private ILPSolver solver;


  private static Heading constraintHeading;
  private static Heading varHeading;
  private static Heading resultHeading;
  private static Heading valuesHeading;


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
    vars = interpreter.createRelationVariable(varHeading);
    newVars = interpreter.createRelationVariable(varHeading);
    solution = model.getSignature().createGroundAtoms();
    atoms = model.getSignature().createGroundAtoms();
    QueryGenerator generator = new QueryGenerator(weights, atoms);
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationVariable variables = interpreter.createRelationVariable(predicate.getHeadingILP());
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
      builder.doubleAttribute("result", "value").num(0.5).doubleGreaterThan();
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
          formula2query.put(formula, generator.generateConstraintQuery(formula, formulas, scores, this,model));
          builder.expr(constraintVariables).from("c");
          builder.expr(lastVarCount).intAttribute("c", "index").intLEQ().where();
          builder.id("index").intAttribute("c", "index").id("weight").doubleAttribute("c", "weight").tuple(2);
          builder.select().query();
          groundFormulaGetWeight.put(formula, builder.getRelation());
        } else {
          formula2query.put(formula, generator.generateConstraintQuery(formula, formulas, scores, this,model));          
        }
      }
    }


  }


  public RelationVariable getNewConstraints() {
    return newConstraints;
  }

  public RelationVariable getConstraints() {
    return constraints;
  }


  public void build(GroundFormulas formulas, GroundAtoms atoms, Scores scores) {
    solver.init();
    interpreter.assign(varCount, builder.num(0).getInt());
    this.formulas.load(formulas);
    this.scores.load(scores);
    this.atoms.load(atoms);
    for (FactorFormula formula : formula2query.keySet()) {
      interpreter.insert(newConstraints, formula2query.get(formula));
      interpreter.insert(constraints, newConstraints);
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

  public void init(Scores scores){
    this.scores.load(scores);
    solver.init();
    clear();
  }

  private void clear() {
    interpreter.clear(constraints);
    interpreter.clear(newConstraints);
    interpreter.clear(vars);
    interpreter.clear(newVars);
    interpreter.assign(varCount, builder.num(0).getInt());
  }

  public void setSolver(ILPSolver solver){
    this.solver = solver;
    clear();
  }


  public ILPSolver getSolver() {
    return solver;
  }

  public boolean changed() {
    return newConstraints.value().size() > 0;
  }


  public RelationVariable getNewVars() {
    return newVars;
  }

  public RelationVariable getVars() {
    return vars;
  }

  public void solve(GroundAtoms solution) {
    solver.add(newVars, newConstraints);
    RelationVariable result = solver.solve();
    extractSolution(result, solution);
  }

  public void extractSolution(RelationVariable result, GroundAtoms solution) {
    this.solution.load(solution);
    interpreter.assign(this.result, result);
    //for all solutions <= 0.5 remove tuples
    //System.out.println(result.value());
    for (UserPredicate predicate : model.getHiddenPredicates())
      interpreter.assign(solution.getGroundAtomsOf(predicate).getRelationVariable(), removeFalseGroundAtoms.get(predicate));

    //for all solutions > 0.5 insert tuples (taken from the corresponding ground atom index table)
    //into the ground atoms
    for (UserPredicate predicate : model.getHiddenPredicates())
      interpreter.insert(solution.getGroundAtomsOf(predicate).getRelationVariable(), addTrueGroundAtoms.get(predicate));

  }

  public void update(GroundFormulas formulas, GroundAtoms atoms) {
    interpreter.assign(lastVarCount, varCount);
    this.formulas.load(formulas);
    this.scores.load(scores);
    this.atoms.load(atoms);
    interpreter.clear(newConstraints);
    for (FactorFormula formula : formula2query.keySet()) {
      RelationExpression query = formula2query.get(formula);
      builder.expr(query);
      builder.expr(constraints);
      interpreter.insert(newConstraints, builder.relationMinus().getRelation());
      interpreter.insert(constraints, newConstraints);
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
}
