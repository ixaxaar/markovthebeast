package thebeast.pml.solve.ilp;

import thebeast.nod.expression.*;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.type.TupleType;
import thebeast.nod.type.RelationType;
import thebeast.nod.statement.Interpreter;
import thebeast.pml.formula.*;
import thebeast.pml.formula.Not;
import thebeast.pml.*;
import thebeast.pml.predicate.*;
import thebeast.pml.function.WeightFunction;
import thebeast.pml.term.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 28-Jun-2007 Time: 16:34:11
 */
public class ILPGrounder {

  private Scores scores;
  private GroundFormulas groundFormulas;
  private IntegerLinearProgram ilp;
  private FactorFormula formula;
  private NoDExpressionGenerator exprGenerator = new NoDExpressionGenerator();
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private ExpressionFactory factory;
  private Weights weights;
  private GroundAtoms groundAtoms;
  private GroundAtoms closure;

  public ILPGrounder() {
    factory = TheBeast.getInstance().getNodServer().expressionFactory();

  }


  public void setClosure(GroundAtoms closure) {
    this.closure = closure;
  }

  public RelationExpression generateCycleConstraints(AcyclicityConstraint acyclicityConstraint) {
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    UserPredicate predicate = acyclicityConstraint.getPredicate();
    //we need to build a query that takes a cycle relation and transforms it into a constraint
    RelationVariable cycle = interpreter.createRelationVariable(predicate.getHeading());
    cycle.setLabel("cycle");
    ExpressionBuilder cycleBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

    cycleBuilder.id("lb").num(Double.NEGATIVE_INFINITY);
    cycleBuilder.id("ub").expr(cycle).count().num(1).intMinus().doubleCast();
    cycleBuilder.id("values");
    cycleBuilder.expr(cycle);
    cycleBuilder.from("cycle");
    //cycleBuilder.expr(ilp.)
    ExpressionBuilder weightBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    weightBuilder.expr(ILPGrounder.this.scores.getScoreRelation(predicate));
    cycleBuilder.id("index");
    cycleBuilder.expr(ILPGrounder.this.ilp.getGroundAtomIndices(predicate));

    int argIndex = 0;
    for (Type type : predicate.getArgumentTypes()) {
      weightBuilder.id(predicate.getAttribute(argIndex).name());
      weightBuilder.attribute("cycle", predicate.getAttribute(argIndex));
      cycleBuilder.id(predicate.getAttribute(argIndex).name());
      cycleBuilder.attribute("cycle", predicate.getAttribute(argIndex++));
    }
    weightBuilder.tuple(predicate.getArity());
    weightBuilder.id("score").num(0.0).tuple(1);
    weightBuilder.get().doubleExtractComponent("score");
    cycleBuilder.tuple(predicate.getArity());
    cycleBuilder.id("index").expr(ILPGrounder.this.ilp.getVarCount()).intPostInc();
    cycleBuilder.id("score").expr(weightBuilder.getExpression());
    cycleBuilder.tuple(2).getPut().intExtractComponent("index");
    cycleBuilder.id("weight").num(1.0);
    cycleBuilder.tuple(2).select();
    cycleBuilder.query();
    cycleBuilder.tuple(3);
    Operator<TupleType> constraint = factory.createOperator("constraint", cycleBuilder.getTuple(), cycle);

    builder.expr(groundFormulas.getCycles(predicate));
    builder.from("cycles");
    builder.attribute("cycles", predicate.getHeadingCycle().attribute("cycle"));
    builder.invokeTupleOp(constraint);
    builder.select();
    builder.query();
    return builder.getRelation();
  }


  public ILPGrounder(Weights weights, GroundAtoms groundAtoms) {
    factory = TheBeast.getInstance().getNodServer().expressionFactory();
    this.weights = weights;
    this.groundAtoms = groundAtoms;
  }

  public RelationExpression generateConstraintQuery(FactorFormula formula, GroundFormulas groundFormulas, final boolean fullyGround,
                                                    final Scores scores, final IntegerLinearProgram ilp,
                                                    final Model model) {
    this.scores = scores;
    this.groundFormulas = groundFormulas;
    this.ilp = ilp;
    this.formula = formula;
    //final QueryGenerator generator = new QueryGenerator(weights, groundAtoms);
    //generator.setClosure(closure);
    //generator.setScores(scores);

    if (formula.getFormula() instanceof AcyclicityConstraint) {
      return generateCycleConstraints((AcyclicityConstraint) formula.getFormula());
    }

    //we only need to consider the formula (not the condition)
    BooleanFormula booleanFormula = this.formula.getFormula();
    //normalize in case we have a deterministic constraint with Negative Infinity weight.
    if (this.formula.isDeterministic() && this.formula.getWeight().isNonPositive())
      booleanFormula = new Not(booleanFormula);
    DNF dnf = DNFGenerator.generateDNF(booleanFormula);
    CNF cnf = CNFGenerator.generateCNF(booleanFormula);
    if (dnf.getConjunctionCount() > 1 && cnf.getDisjunctionCount() > 1)
      throw new UnsupportedFormulaException("Formula must be transformable to atomic disjunction or conjuntion",
              this.formula);

    final ExpressionBuilder varBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    final ExpressionBuilder varSetBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    final ExpressionBuilder constraintBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    final HashMap<Variable, Expression> var2expr = new HashMap<Variable, Expression>();
    constraintBuilder.expr(this.groundFormulas.getNewGroundFormulas(this.formula)).from("formulas");
    if (formula.usesWeights()) {
      double eps = 1E-10;
      constraintBuilder.expr(weights.getWeights()).intAttribute("formulas", "index").doubleArrayElement();
      constraintBuilder.num(eps).doubleLessThan();
      constraintBuilder.expr(weights.getWeights()).intAttribute("formulas", "index").doubleArrayElement();
      constraintBuilder.num(-eps).doubleGreaterThan();
      constraintBuilder.and(2).not().where();
    }
    int varIndex = 0;
    for (Variable var : this.formula.getQuantification().getVariables()) {
      var2expr.put(var, factory.createAttribute("formulas", this.formula.getQuantification().getAttribute(varIndex++)));
    }

    final boolean useDisjunction = cnf.getDisjunctionCount() == 1;
    final List<SignedAtom> atoms = useDisjunction ? cnf.getDisjunction(0) : dnf.getConjunction(0);
    boolean[] signs = useDisjunction ? cnf.getSigns(0) : dnf.getSigns(0);
    final LinkedList<Boolean> signsWithoutCardinalities = new LinkedList<Boolean>();
    if (!this.formula.isDeterministic()) {
      //here we create an expression which will return the index of
      //the current feature if it exists in the ilp Or creates/puts a new
      //row in the ilp map with the proper weight and a new index.
      FunctionApplication weight = (FunctionApplication) this.formula.getWeight();
      WeightFunction weightFunction = (WeightFunction) weight.getFunction();
      varBuilder.expr(this.ilp.getGroundFormulaIndices(this.formula));
      varIndex = 0;
      for (Variable var : this.formula.getQuantification().getVariables()) {
        varBuilder.id(this.formula.getQuantification().getAttribute(varIndex++).name());
        varBuilder.expr(var2expr.get(var));
      }
      varBuilder.tuple(this.formula.getQuantification().getVariables().size());
      varBuilder.id("index").expr(this.ilp.getVarCount()).intPostInc();
      varBuilder.id("weight");
      int argIndex = 0;
      varBuilder.expr(weights.getWeights());
      varBuilder.expr(weights.getRelation(weightFunction));
      for (Term arg : weight.getArguments()) {
        varBuilder.id(weightFunction.getAttributeForArg(argIndex++).name());
        varBuilder.expr(exprGenerator.convertTerm(arg, groundAtoms, weights, var2expr, null));
      }
      varBuilder.tuple(weight.getArguments().size());
      varBuilder.id("index").num(-1).tuple(1).get().intExtractComponent("index");
      varBuilder.doubleArrayElement();
      varBuilder.tuple(2);
      varBuilder.getPut().intExtractComponent("index");
    }

    final LinkedList<RelationExpression> leqQueries = new LinkedList<RelationExpression>();
    final LinkedList<IntExpression> upperBounds = new LinkedList<IntExpression>();
    final LinkedList<RelationExpression> geqQueries = new LinkedList<RelationExpression>();
    final LinkedList<IntExpression> lowerBounds = new LinkedList<IntExpression>();

    //the atom argument indices
    for (final SignedAtom signedAtom : atoms) {
      signedAtom.getAtom().acceptAtomVisitor(new AbstractAtomVisitor() {
        public void visitPredicateAtom(final PredicateAtom predicateAtom) {
          signsWithoutCardinalities.add(signedAtom.isTrue());
          predicateAtom.getPredicate().acceptPredicateVisitor(new AbstractPredicateVisitor() {
            public void visitUserPredicate(UserPredicate userPredicate) {
              ExpressionBuilder weightBuilder = varBuilder.createBuilder();
              weightBuilder.expr(ILPGrounder.this.scores.getScoreRelation(userPredicate));
              varBuilder.expr(ILPGrounder.this.ilp.getGroundAtomIndices(userPredicate));
              int argIndex = 0;
              for (Term arg : predicateAtom.getArguments()) {
                Expression expression = exprGenerator.convertTerm(arg, groundAtoms, weights, var2expr, null);
                weightBuilder.id(userPredicate.getAttribute(argIndex).name());
                weightBuilder.expr(expression);
                varBuilder.id(userPredicate.getAttribute(argIndex++).name());
                varBuilder.expr(expression);
              }
              weightBuilder.tuple(predicateAtom.getArguments().size());
              weightBuilder.id("score").num(0.0).tuple(1);
              weightBuilder.get().doubleExtractComponent("score");
              varBuilder.tuple(predicateAtom.getArguments().size());
              varBuilder.id("index").expr(ILPGrounder.this.ilp.getVarCount()).intPostInc();
              varBuilder.id("score").expr(weightBuilder.getExpression());
              varBuilder.tuple(2);
              varBuilder.getPut().intExtractComponent("index");
            }
          });
        }

        public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
          if (!signedAtom.isTrue()) {
            cardinalityConstraint = cardinalityConstraint.negate();
            //throw new RuntimeException("Cardinality constraints must be non-negated");
          }
//          if (atoms.size() > 1 && useDisjunction && !cardinalityConstraint.isGEQ() ||
//                  atoms.size() > 1 && !useDisjunction && !cardinalityConstraint.isLEQ())
//            throw new RuntimeException("Sorry, we can't do LEQ constraints in disjunctions or" +
//                    "GEQ constraints in conjunctions yet");
          //if the formula is deterministic we can actually spell out the disjunction
          if (!cardinalityConstraint.isGEQ() && !cardinalityConstraint.isLEQ()) {
            throw new RuntimeException("Sorry, we can't do cardinality constraints with lower and upper bound" +
                    "yet");
          }
          if (cardinalityConstraint.isLEQ()) {
            leqQueries.add(createLEQQuery(cardinalityConstraint, var2expr, fullyGround, model, 1.0));
            upperBounds.add((IntExpression) exprGenerator.convertTerm(cardinalityConstraint.getUpperBound(),
                    groundAtoms, weights, var2expr, null));
            //create a query which selects ilp variable indices
          } else if (cardinalityConstraint.isGEQ()) {
            geqQueries.add(createGEQQuery(cardinalityConstraint, var2expr, model));
            lowerBounds.add((IntExpression) exprGenerator.convertTerm(cardinalityConstraint.getLowerBound(),
                    groundAtoms, weights, var2expr, null));
            //create a query which selects ilp variable indices
          }

          //todo: take care of the sign
          //and the cardinality constraint
          //add
        }

        public void visitTrue(True aTrue) {

        }
      });
    }
    //call the constraint operator to generate constraints.
    if (!this.formula.isDeterministic()) {
      if (leqQueries.size() == 0 && geqQueries.size() == 0) {
        constraintBuilder.expressions(varBuilder.lastExpressions(atoms.size() + 1));
        constraintBuilder.invokeRelOp(generateSoftConstraintOperator(useDisjunction, signs));
      } else if (leqQueries.size() == 1 && geqQueries.size() == 0) {
        if (useDisjunction) {
          RelationExpression items = leqQueries.get(0);
          IntExpression upperBound = upperBounds.get(0);
          constraintBuilder.expr(items);
          constraintBuilder.expr(upperBound);
          //the varBuilder contains |weight index| atom1 | atom2| ... where the cardinality constraint atom is removed 
          constraintBuilder.expressions(varBuilder.lastExpressions(atoms.size()));
          constraintBuilder.invokeRelOp(generateLEQSoftCardinalityConstraintOperator(
                  useDisjunction, signsWithoutCardinalities));

        }
      } else if (leqQueries.size() == 0 && geqQueries.size() == 1) {
        if (useDisjunction) {
          RelationExpression items = geqQueries.get(0);
          IntExpression lowerBound  = lowerBounds.get(0);
          constraintBuilder.expr(items);
          constraintBuilder.expr(lowerBound);
          //the varBuilder contains |weight index| atom1 | atom2| ... where the cardinality constraint atom is removed
          constraintBuilder.expressions(varBuilder.lastExpressions(atoms.size()));
          constraintBuilder.invokeRelOp(generateGEQSoftCardinalityConstraintOperator(
                  useDisjunction, signsWithoutCardinalities));

        }
      }
    } else {
      if (leqQueries.size() == 0 && geqQueries.size() == 0) {
        constraintBuilder.expressions(varBuilder.lastExpressions(atoms.size()));
        constraintBuilder.invokeRelOp(generateHardConstraintOperator(useDisjunction, signs));
      } else if (leqQueries.size() == 1 && geqQueries.size() == 0 && useDisjunction) {
        RelationExpression items = leqQueries.get(0);
        IntExpression upperBound = upperBounds.get(0);
        constraintBuilder.expr(items);
        constraintBuilder.expr(upperBound);
        constraintBuilder.expressions(varBuilder.lastExpressions(atoms.size() - 1));
        constraintBuilder.invokeRelOp(generateLEQHardCardinalityConstraintOperator(
                useDisjunction, signsWithoutCardinalities));
      } else if (geqQueries.size() == 1 && leqQueries.size() == 0 && useDisjunction) {
        RelationExpression items = geqQueries.get(0);
        IntExpression lowerBound = lowerBounds.get(0);
        constraintBuilder.expr(items);
        constraintBuilder.expr(lowerBound);
        constraintBuilder.expressions(varBuilder.lastExpressions(atoms.size() - 1));
        constraintBuilder.invokeRelOp(generateGEQHardCardinalityConstraintOperator(
                useDisjunction, signsWithoutCardinalities));
      }
    }

    constraintBuilder.insert();

    return constraintBuilder.queryInsert().getRelation();
  }

  public Operator<RelationType> generateSoftConstraintOperator(boolean disjunction, boolean[] signs) {
    int size = signs.length;
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
    IntVariable f = interpreter.createIntVariable();
    IntVariable[] variables = new IntVariable[size];
    int trueCount = 0;
    for (int i = 0; i < size; ++i) {
      variables[i] = interpreter.createIntVariable();
      if (signs[i]) ++trueCount;
    }
    int falseCount = size - trueCount;
    //the 'big' constraint
    //TODO: this is not enough to turn this into a conjunction, further changes below are necessary
    builder.id("lb").doubleValue(disjunction ? -falseCount : 1 - trueCount);
    builder.id("ub").num(Double.POSITIVE_INFINITY);
    builder.id("values");
    double scale = disjunction ? 1.0 : -1.0;
    builder.id("index").expr(f).id("weight").num(-1.0 * scale).tuple(2);
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables[i]).id("weight").num(scale * (signs[i] ? 1.0 : -1.0)).tuple(2);
    }
    builder.relation(size + 1, false);
    builder.tuple(3);
    //the 'small' constraints
    for (int i = 0; i < size; ++i) {
      builder.id("lb").num(disjunction ? signs[i] ? 0.0 : 1.0 : signs[i] ? 0.0 : -1.0);
      builder.id("ub").num(Double.POSITIVE_INFINITY);
      builder.id("values");
      builder.id("index").expr(f).id("weight").num(scale).tuple(2);
      builder.id("index").expr(variables[i]).id("weight").num(signs[i] ? -scale : scale).tuple(2);
      builder.relation(2, false);
      builder.tuple(3);
    }
    builder.relation(size + 1);
    LinkedList<IntVariable> all = new LinkedList<IntVariable>();
    all.add(f);
    for (IntVariable v : variables) all.add(v);
    return factory.createOperator("constraints", all, builder.getRelation());
  }

  public Operator<RelationType> generateHardConstraintOperator(boolean disjunction, boolean[] signs) {
    int size = signs.length;
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
    IntVariable[] variables = new IntVariable[size];
    int trueCount = 0;
    for (int i = 0; i < size; ++i) {
      variables[i] = interpreter.createIntVariable();
      if (signs[i]) ++trueCount;
    }
    int falseCount = size - trueCount;
    builder.id("lb").doubleValue(disjunction ? 1 - falseCount : size - falseCount);
    builder.id("ub").num(Double.POSITIVE_INFINITY);
    builder.id("values");
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables[i]).id("weight").num(signs[i] ? 1.0 : -1.0).tuple(2);
    }
    builder.relation(size, false);
    builder.tuple(3);
    builder.relation(1, false);
    LinkedList<IntVariable> all = new LinkedList<IntVariable>();
    for (IntVariable v : variables) all.add(v);
    return factory.createOperator("constraints", all, builder.getRelation());
  }

  public Operator<RelationType> generateGEQHardCardinalityConstraintOperator(boolean disjunction, List<Boolean> signs) {
    if (!disjunction) throw new RuntimeException("Can't do conjunctions with card. constraints yet");
    int size = signs.size();
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
    LinkedList<thebeast.nod.variable.Variable> variables = new LinkedList<thebeast.nod.variable.Variable>();
    RelationVariable items = interpreter.createRelationVariable(IntegerLinearProgram.getValuesHeading());
    items.setLabel("items");
    variables.add(items);
    IntVariable lowerBound = interpreter.createIntVariable();
    lowerBound.setLabel("lowerBound");
    variables.add(lowerBound);
    int trueCount = 0;
    for (int i = 0; i < size; ++i) {
      IntVariable var = interpreter.createIntVariable();
      var.setLabel("var" + i);
      variables.add(var);
      if (signs.get(i)) ++trueCount;
    }
    int falseCount = size - trueCount;
    builder.id("lb").expr(lowerBound).doubleCast().num(1.0).doubleValue(falseCount).doubleMinus().doubleTimes();
    builder.id("ub").num(Double.POSITIVE_INFINITY);
    builder.id("values");
    builder.expr(items);
    //builder.id("")
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables.get(i + 2));
      builder.id("weight").expr(lowerBound).doubleCast();
      if (!signs.get(i))
        builder.num(-1.0).doubleTimes();
      builder.tuple(2);
    }
    if (size > 0) {
      builder.relation(size);
      builder.union(2);
    }
    builder.tuple(3);
    builder.relation(1);
    return factory.createOperator("constraints", variables, builder.getRelation());
  }


  public Operator<RelationType> generateLEQHardCardinalityConstraintOperator(boolean disjunction, List<Boolean> signs) {
    if (!disjunction) throw new RuntimeException("Can't do conjunctions with card. constraints yet");
    int size = signs.size();
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
    LinkedList<thebeast.nod.variable.Variable> variables = new LinkedList<thebeast.nod.variable.Variable>();
    RelationVariable items = interpreter.createRelationVariable(IntegerLinearProgram.getValuesHeading());
    items.setLabel("items");
    variables.add(items);
    IntVariable upperBound = interpreter.createIntVariable();
    upperBound.setLabel("upperbound");
    variables.add(upperBound);
    int trueCount = 0;
    for (int i = 0; i < size; ++i) {
      IntVariable var = interpreter.createIntVariable();
      var.setLabel("var" + i);
      variables.add(var);
      if (signs.get(i)) ++trueCount;
    }
    int falseCount = size - trueCount;
    builder.id("lb").num(Double.NEGATIVE_INFINITY);
    builder.id("ub").expr(upperBound).doubleCast().expr(items).count().doubleCast().doubleAdd().doubleValue(falseCount).
            doubleTimes().expr(upperBound).doubleCast().doubleAdd();
    builder.id("values");
    builder.expr(items);
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables.get(i + 2));
      builder.id("weight").expr(upperBound).doubleCast().expr(items).count().doubleCast().doubleAdd();
      if (signs.get(i))
        builder.num(-1.0).doubleTimes();
      builder.tuple(2);
    }
    if (size > 0) {
      builder.relation(size);
      builder.union(2);
    }
    builder.tuple(3);
    builder.relation(1);
    return factory.createOperator("constraints", variables, builder.getRelation());
  }

  //takes items | upperbound | weight_index | atoms
  public Operator<RelationType> generateLEQSoftCardinalityConstraintOperator(boolean disjunction, List<Boolean> signs) {
    if (!disjunction) throw new RuntimeException("Can't do conjunctions with card. constraints yet");

    /*
      x1 + ... + xn <= k | a1 | ... | am
      c = x1 + ... + xn;
      a = a1 + ... + am;

      1) (n-c)/(n-k) + a + (1-s) >= 1
         c - a * (n-k) + s * (n-k) <= n
      2) -((n-c+1)/(n-k) + a - 1)/((n+1)/(n-k) + m - 1) + s >= 0
         //c - a * (n-k) + s * (n/(n-k) + m - 1) * (n-k) >= k
         c - a * (n-k) + s * ((n+1)/(n-k) + m - 1) * (n-k) >= k + 1
    */

    int size = signs.size();
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
    LinkedList<thebeast.nod.variable.Variable> variables = new LinkedList<thebeast.nod.variable.Variable>();
    RelationVariable items = interpreter.createRelationVariable(IntegerLinearProgram.getValuesHeading());
    items.setLabel("items");
    variables.add(items);
    IntExpression n = builder.expr(items).count().getInt();
    IntVariable k = interpreter.createIntVariable();
    DoubleExpression k_plus_1 = builder.expr(k).num(1).intAdd().doubleCast().getDouble();
    DoubleExpression n_plus_1 = builder.expr(n).num(1).intAdd().doubleCast().getDouble();
    IntExpression n_minus_k = builder.expr(n).expr(k).intMinus().getInt();
    DoubleExpression max = builder.expr(n_plus_1).expr(n_minus_k).doubleCast().doubleDivide().
            doubleValue(size-1).doubleAdd().getDouble();
    k.setLabel("upperbound");
    variables.add(k);
    IntVariable weightIndex = interpreter.createIntVariable();
    variables.add(weightIndex);
    int trueCount = 0;
    for (int i = 0; i < size; ++i) {
      IntVariable var = interpreter.createIntVariable();
      var.setLabel("var" + i);
      variables.add(var);
      if (signs.get(i)) ++trueCount;
    }
    int falseCount = size - trueCount;
    //c - a * (n-k) + s * (n-k) <= n
    builder.id("lb").num(Double.NEGATIVE_INFINITY);
    //upperbound = falsecount * (n-k) + n
    builder.id("ub").doubleValue(falseCount).expr(n_minus_k).doubleCast().doubleTimes().expr(n).doubleCast().doubleAdd();
    builder.id("values");
    builder.expr(items);
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables.get(i + 3));
      builder.id("weight").expr(n_minus_k).doubleCast();
      if (signs.get(i))
        builder.num(-1.0).doubleTimes();
      builder.tuple(2);
    }
    builder.id("index").expr(weightIndex);
    builder.id("weight").expr(n_minus_k).doubleCast();
    builder.tuple(2);
    builder.relation(size + 1); //put atoms and weight variable in one relation
    //TODO: what if some atoms to count and atoms in disjunction are identical? union won't work!
    builder.union(2); //combine with atoms from cardinality constraint
    builder.tuple(3);

    //now c - a * (n-k) + s * (n/(n-k) + m - 1) * (n-k) >= k
    builder.id("ub").num(Double.POSITIVE_INFINITY);
    //lowerbound = k + 1 + falsecount * (n-k)
    builder.id("lb").doubleValue(falseCount).expr(n_minus_k).doubleCast().doubleTimes().expr(k_plus_1).doubleAdd();
    builder.id("values");
    builder.expr(items);
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables.get(i + 3));
      builder.id("weight").expr(n_minus_k).doubleCast();
      if (signs.get(i))
        builder.num(-1.0).doubleTimes();
      builder.tuple(2);
    }
    builder.id("index").expr(weightIndex);
    //weight = (n/(n-k) + m - 1) * (n-k)
    builder.id("weight").expr(n_minus_k).doubleCast().expr(max).doubleTimes();
    builder.tuple(2);
    builder.relation(size + 1); //put atoms and weight variable in one relation
    //TODO: what if some atoms to count and atoms in disjunction are identical? union won't work!
    builder.union(2); //combine with atoms from cardinality constraint
    builder.tuple(3);

    builder.relation(2);
    return factory.createOperator("constraints", variables, builder.getRelation());
  }

  public Operator<RelationType> generateGEQSoftCardinalityConstraintOperator(boolean disjunction, List<Boolean> signs) {
    if (!disjunction) throw new RuntimeException("Can't do conjunctions with card. constraints yet");

    /*

      1) c/k + a + (1-s) >= 1
         c + a * k + k - k * s >=k
         c + a * k - k * s >= k - k = 0

      2) -((c+1)/k + a - 1)/((n+1)/k + m - 1) + s >= 0
         -((c+1)/k + a - 1) + s * ((n+1)/k + m - 1) >=0
         -(c+1)/k - a + 1 + s * ((n+1)/k + m - 1) >=0
         max = ((n+1)/k + m - 1)
         -c - 1 - k * a + k + s * max * k >=0
         -c - k*a + s * max * k >= 1 - k
    */

    int size = signs.size();
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
    LinkedList<thebeast.nod.variable.Variable> variables = new LinkedList<thebeast.nod.variable.Variable>();
    RelationVariable items = interpreter.createRelationVariable(IntegerLinearProgram.getValuesHeading());
    items.setLabel("items");
    variables.add(items);
    IntExpression n = builder.expr(items).count().getInt();
    IntVariable k = interpreter.createIntVariable();
    DoubleExpression k_plus_1 = builder.expr(k).num(1).intAdd().doubleCast().getDouble();
    DoubleExpression n_plus_1 = builder.expr(n).num(1).intAdd().doubleCast().getDouble();
    IntExpression n_minus_k = builder.expr(n).expr(k).intMinus().getInt();
    DoubleExpression max = builder.expr(n_plus_1).expr(k).doubleCast().doubleDivide().
            doubleValue(size-1).doubleAdd().getDouble();
    k.setLabel("upperbound");
    variables.add(k);
    IntVariable weightIndex = interpreter.createIntVariable();
    variables.add(weightIndex);
    int trueCount = 0;
    for (int i = 0; i < size; ++i) {
      IntVariable var = interpreter.createIntVariable();
      var.setLabel("var" + i);
      variables.add(var);
      if (signs.get(i)) ++trueCount;
    }
    int falseCount = size - trueCount;

    builder.id("ub").num(Double.POSITIVE_INFINITY);
    //c + a * k - k * s >= k - k = 0

    //c + a * k - k * s >= 1 - k

    //lowerbound =  - falsecount * k
    builder.id("lb").doubleValue(-falseCount).expr(k).doubleCast().doubleTimes();
    builder.id("values");
    builder.expr(items);
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables.get(i + 3));
      builder.id("weight").expr(k).doubleCast();
      if (!signs.get(i))
        builder.num(-1.0).doubleTimes();
      builder.tuple(2);
    }
    builder.id("index").expr(weightIndex);
    builder.id("weight").expr(k).doubleCast().num(-1.0).doubleTimes();
    builder.tuple(2);
    builder.relation(size + 1); //put atoms and weight variable in one relation
    //TODO: what if some atoms to count and atoms in disjunction are identical? union won't work!
    builder.union(2); //combine with atoms from cardinality constraint
    builder.tuple(3);

    //-c - k*a + s * max * k >= 1 - k
    //c + k*a - s * max * k <= k - 1
    builder.id("lb").num(Double.NEGATIVE_INFINITY);
    //upperbound = k - 1 - falsecount * k = k * (1 - falsecount) - 1
    builder.id("ub").doubleValue(1 - falseCount).expr(k).doubleCast().doubleTimes().num(-1.0).doubleAdd();
    builder.id("values");
    builder.expr(items);
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables.get(i + 3));
      builder.id("weight").expr(k).doubleCast();
      if (!signs.get(i))
        builder.num(-1.0).doubleTimes();
      builder.tuple(2);
    }
    builder.id("index").expr(weightIndex);
    //weight = - max * k
    builder.id("weight").expr(k).doubleCast().expr(max).num(-1.0).doubleTimes().doubleTimes();
    builder.tuple(2);
    builder.relation(size + 1); //put atoms and weight variable in one relation
    //TODO: what if some atoms to count and atoms in disjunction are identical? union won't work!
    builder.union(2); //combine with atoms from cardinality constraint
    builder.tuple(3);

    builder.relation(2);
    return factory.createOperator("constraints", variables, builder.getRelation());
  }



  public RelationExpression createLEQQuery(CardinalityConstraint constraint,
                                           Map<Variable, Expression> var2expr, boolean useAll,
                                           final Model model, double itemWeight) {
    Quantification quantification = constraint.getQuantification();
    BooleanFormula formula = constraint.getFormula();
    DNF dnf = DNFGenerator.generateDNF(formula);
    if (dnf.getConjunctionCount() > 1)
      throw new RuntimeException("We can only do plain conjunctions for cardinality constraints but look at this:" +
              dnf + " coming from this " + formula);
    List<SignedAtom> conjunction = dnf.getConjunction(0);

    ConjunctionProcessor processor = new ConjunctionProcessor(weights, groundAtoms);


    UnresolvedVariableCollector collector = new UnresolvedVariableCollector();
    collector.bind(quantification.getVariables());

    formula.acceptBooleanFormulaVisitor(collector);

    LinkedList<Expression> args = new LinkedList<Expression>();
    LinkedList<thebeast.nod.variable.Variable> params = new LinkedList<thebeast.nod.variable.Variable>();
    final ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
    context.var2expr.putAll(var2expr);
    for (Variable var : collector.getUnresolved()) {
      thebeast.nod.variable.Variable param = interpreter.createVariable(var.getType().getNodType());
      param.setLabel(var.getName());
      Expression arg = var2expr.get(var);
      args.add(arg);
      params.add(param);
      //make sure the processor can replace this variable with an actual expression (the param)
      context.var2expr.put(var, param);
      //make sure the conjunction processor is able to resolve the externally bound variables
      context.var2term.put(var, var);
    }

    SignedAtom hidden = null;
    if (useAll) {
      for (SignedAtom atom : conjunction) {
        if (atom.getAtom() instanceof PredicateAtom) {
          PredicateAtom predicateAtom = (PredicateAtom) atom.getAtom();
          UserPredicate predicate = (UserPredicate) predicateAtom.getPredicate();
          if (model.getHiddenPredicates().contains(predicate))
            hidden = atom;
        }
      }
      conjunction.remove(hidden);

    }

    processor.processConjunction(context, conjunction);

    if (useAll) {
      ArrayList<SignedAtom> hiddenList = new ArrayList<SignedAtom>();
      hiddenList.add(hidden);
      //processor.resolveBruteForce(context, hiddenList);
      processor.resolveBruteForce(context, hiddenList);
      conjunction.add(hidden);
    }

    //find the hidden ground atom
    context.selectBuilder.id("index");
    for (SignedAtom atom : conjunction)
      atom.getAtom().acceptAtomVisitor(new AbstractAtomVisitor() {
        public void visitPredicateAtom(final PredicateAtom predicateAtom) {
          predicateAtom.getPredicate().acceptPredicateVisitor(new AbstractPredicateVisitor() {
            public void visitUserPredicate(UserPredicate userPredicate) {
              if (model.getHiddenPredicates().contains(userPredicate)) {
                ExpressionBuilder weightBuilder = context.selectBuilder.createBuilder();
                weightBuilder.expr(ILPGrounder.this.scores.getScoreRelation(userPredicate));
                context.selectBuilder.expr(ILPGrounder.this.ilp.getGroundAtomIndices(userPredicate));
                int argIndex = 0;
                for (Term term : predicateAtom.getArguments()) {
                  TermResolver resolver = new TermResolver();
                  Term resolved = resolver.resolve(term, context.var2term);
                  Expression expression = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, null);
                  weightBuilder.id(userPredicate.getAttribute(argIndex).name());
                  weightBuilder.expr(expression);
                  context.selectBuilder.id(userPredicate.getAttribute(argIndex++).name());
                  context.selectBuilder.expr(expression);
                }
                weightBuilder.tuple(predicateAtom.getArguments().size());
                weightBuilder.id("score").num(0.0).tuple(1);
                weightBuilder.get().doubleExtractComponent("score");
                context.selectBuilder.tuple(predicateAtom.getArguments().size());
                context.selectBuilder.id("index").expr(ILPGrounder.this.ilp.getVarCount()).intPostInc();
                context.selectBuilder.id("score").expr(weightBuilder.getExpression());
                context.selectBuilder.tuple(2);
                context.selectBuilder.getPut().intExtractComponent("index");

              }
            }
          });
        }
      });
    context.selectBuilder.id("weight").num(itemWeight);
    context.selectBuilder.tuple(2);

    BoolExpression where = factory.createAnd(context.conditions);
    Query query = factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple());
    Operator<RelationType> op = factory.createOperator("items", params, query);
    return factory.createRelationOperatorInv(op, args);
//    builder.expressions(args).invokeIntOp(op);
//
//    //for (BoolExpression condition : context.conditions) builder.expr(condition);
//    BoolExpression where = factory.createAnd(context.conditions);
//    return factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple());
  }


  public RelationExpression createGEQQuery(CardinalityConstraint constraint,
                                           Map<Variable, Expression> var2expr,
                                           final Model model) {
    Quantification quantification = constraint.getQuantification();
    BooleanFormula formula = constraint.getFormula();
    DNF dnf = DNFGenerator.generateDNF(formula);
    if (dnf.getConjunctionCount() > 1)
      throw new RuntimeException("We can only do plain conjunctions for cardinality constraints but look at this:" +
              dnf + " coming from this " + formula);
    List<SignedAtom> conjunction = dnf.getConjunction(0);


    UnresolvedVariableCollector collector = new UnresolvedVariableCollector();
    collector.bind(quantification.getVariables());

    formula.acceptBooleanFormulaVisitor(collector);

    LinkedList<Expression> args = new LinkedList<Expression>();
    LinkedList<thebeast.nod.variable.Variable> params = new LinkedList<thebeast.nod.variable.Variable>();
    final ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
    context.var2expr.putAll(var2expr);
    for (Variable var : collector.getUnresolved()) {
      thebeast.nod.variable.Variable param = interpreter.createVariable(var.getType().getNodType());
      param.setLabel(var.getName());
      Expression arg = var2expr.get(var);
      args.add(arg);
      params.add(param);
      //make sure the processor can replace this variable with an actual expression (the param)
      context.var2expr.put(var, param);
      //make sure the conjunction processor is able to resolve the externally bound variables
      context.var2term.put(var, var);
    }

    //remove hidden atom from the conjunction (and add all constants expressions for unbound categoricals)

    SignedAtom hidden = null;
    for (SignedAtom atom : conjunction) {
      if (atom.getAtom() instanceof PredicateAtom) {
        PredicateAtom predicateAtom = (PredicateAtom) atom.getAtom();
        UserPredicate predicate = (UserPredicate) predicateAtom.getPredicate();
        if (model.getHiddenPredicates().contains(predicate))
          hidden = atom;
      }
    }
    if (hidden == null) throw new RuntimeException("Exactly one atom in the cardinality formula should be hidden");
    conjunction.remove(hidden);

    ConjunctionProcessor processor = new ConjunctionProcessor(weights, groundAtoms);

    processor.processConjunction(context, conjunction);

    ArrayList<SignedAtom> hiddenList = new ArrayList<SignedAtom>();
    hiddenList.add(hidden);
    //processor.resolveBruteForce(context, hiddenList);
    if (constraint.useClosure())
      processor.processWithClosure(context, closure, hidden);
    else
      processor.resolveBruteForce(context, hiddenList);

    //find the hidden ground atom
    context.selectBuilder.id("index");

    hidden.getAtom().acceptAtomVisitor(new AbstractAtomVisitor() {
      public void visitPredicateAtom(final PredicateAtom predicateAtom) {
        predicateAtom.getPredicate().acceptPredicateVisitor(new AbstractPredicateVisitor() {
          public void visitUserPredicate(UserPredicate userPredicate) {
            if (model.getHiddenPredicates().contains(userPredicate)) {
              ExpressionBuilder weightBuilder = context.selectBuilder.createBuilder();
              weightBuilder.expr(ILPGrounder.this.scores.getScoreRelation(userPredicate));
              context.selectBuilder.expr(ILPGrounder.this.ilp.getGroundAtomIndices(userPredicate));
              int argIndex = 0;
              for (Term term : predicateAtom.getArguments()) {
                TermResolver resolver = new TermResolver();
                Term resolved = resolver.resolve(term, context.var2term);
                Expression expression = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, null);
                weightBuilder.id(userPredicate.getAttribute(argIndex).name());
                weightBuilder.expr(expression);
                context.selectBuilder.id(userPredicate.getAttribute(argIndex++).name());
                context.selectBuilder.expr(expression);
              }
              weightBuilder.tuple(predicateAtom.getArguments().size());
              weightBuilder.id("score").num(0.0).tuple(1);
              weightBuilder.get().doubleExtractComponent("score");
              context.selectBuilder.tuple(predicateAtom.getArguments().size());
              context.selectBuilder.id("index").expr(ILPGrounder.this.ilp.getVarCount()).intPostInc();
              context.selectBuilder.id("score").expr(weightBuilder.getExpression());
              context.selectBuilder.tuple(2);
              context.selectBuilder.getPut().intExtractComponent("index");

            }
          }

        

        });
      }
    });
    context.selectBuilder.id("weight").num(1.0);
    context.selectBuilder.tuple(2);

    BoolExpression where = factory.createAnd(context.conditions);
    Query query = factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple());
    Operator<RelationType> op = factory.createOperator("items", params, query);
    return factory.createRelationOperatorInv(op, args);
//    builder.expressions(args).invokeIntOp(op);
//
//    //for (BoolExpression condition : context.conditions) builder.expr(condition);
//    BoolExpression where = factory.createAnd(context.conditions);
//    return factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple());
  }


}
