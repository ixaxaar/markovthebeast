package thebeast.pml.formula;

import thebeast.nod.expression.*;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.TupleType;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.*;
import thebeast.pml.*;
import thebeast.pml.predicate.*;
import thebeast.pml.function.*;
import thebeast.pml.term.*;
import thebeast.pml.term.IntConstant;
import thebeast.pml.term.CategoricalConstant;
import thebeast.pml.term.DoubleConstant;
import thebeast.pml.term.Variable;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class QueryGenerator {
  private Scores scores;
  private GroundFormulas groundFormulas;
  private IntegerLinearProgram ilp;
  private FactorFormula formula;

  private static class ConjunctionContext {
    private LinkedList<BoolExpression> conditions = new LinkedList<BoolExpression>();
    private HashMap<Variable, Expression> var2expr = new HashMap<Variable, Expression>();
    private HashMap<Variable, Term> var2term = new HashMap<Variable, Term>();
    private LinkedList<RelationVariable> relations = new LinkedList<RelationVariable>();
    private LinkedList<String> prefixes = new LinkedList<String>();
    private ExpressionBuilder selectBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    private HashMap<String, Term> remainingHiddenArgs = new HashMap<String, Term>();

  }

  private DNFGenerator dnfGenerator = new DNFGenerator();
  private CNFGenerator cnfGenerator = new CNFGenerator();
  private TermResolver termResolver = new TermResolver();
  private FormulaResolver formulaResolver = new FormulaResolver();
  private TermInverter inverter = new TermInverter();
  private NoDExpressionGenerator exprGenerator = new NoDExpressionGenerator();
  private GroundAtoms groundAtoms;
  private Weights weights;
  private ExpressionFactory factory;
  private FormulaBuilder builder;
  private ExpressionBuilder exprBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private LinkedList<ConjunctionProcessor.Context> conjunctions;


  public QueryGenerator() {
    factory = TheBeast.getInstance().getNodServer().expressionFactory();
  }

  public QueryGenerator(Weights weights, GroundAtoms groundAtoms) {
    factory = TheBeast.getInstance().getNodServer().expressionFactory();
    this.weights = weights;
    this.groundAtoms = groundAtoms;
  }


  public RelationExpression generateGlobalTrueQuery(FactorFormula factorFormula, GroundAtoms groundAtoms, Weights w) {
    this.groundAtoms = groundAtoms;
    this.weights = w;
    builder = new FormulaBuilder(groundAtoms.getSignature());

    BooleanFormula condition = factorFormula.getCondition();
    BooleanFormula both = condition == null ?
            factorFormula.getFormula() : new Conjunction(condition, factorFormula.getFormula());
    processGlobalFormula(both, factorFormula);
    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    for (ConjunctionProcessor.Context context : conjunctions) {
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);
  }

  public RelationExpression generateGlobalFalseQuery(FactorFormula factorFormula, GroundAtoms groundAtoms, Weights w) {
    this.groundAtoms = groundAtoms;
    this.weights = w;
    builder = new FormulaBuilder(groundAtoms.getSignature());

    BooleanFormula condition = factorFormula.getCondition();

    BooleanFormula negated = new Not(factorFormula.getFormula());
    BooleanFormula both = condition == null ?
            negated : new Conjunction(condition, negated);
    processGlobalFormula(both, factorFormula);
    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    for (ConjunctionProcessor.Context context : conjunctions) {
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);
  }

  public RelationExpression generateLocalFeatureExtractor(FactorFormula factorFormula, GroundAtoms groundAtoms, Weights w) {
    this.groundAtoms = groundAtoms;
    this.weights = w;
    builder = new FormulaBuilder(groundAtoms.getSignature());

    WeightFunction weightFunction = (WeightFunction) ((FunctionApplication) factorFormula.getWeight()).getFunction();
    BooleanFormula condition = factorFormula.getCondition();

    BooleanFormula both = condition == null ?
            factorFormula.getFormula() : new Conjunction(condition, factorFormula.getFormula());
    processGlobalFormula(both, factorFormula);

    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    for (ConjunctionProcessor.Context context : conjunctions) {
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);
  }


  private void processGlobalFormula(BooleanFormula both, FactorFormula factorFormula) {
    DNF dnf = DNFGenerator.generateDNF(both);
    conjunctions = new LinkedList<ConjunctionProcessor.Context>();
    ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);

    for (List<SignedAtom> conjunction : dnf.getConjunctions()) {

      //create conjunction context
      final ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
      conjunctions.add(context);

      //we process the weights first
      processWeightForGlobal(context, factorFormula.getWeight());

      //process the condition conjunction
      conjunctionProcessor.processConjunction(context, conjunction);

      //now add the quantification variables as attributes
      int index = 0;
      for (Variable var : factorFormula.getQuantification().getVariables()) {
        Term term = context.var2term.get(var);
        Expression expression = exprGenerator.convertTerm(term, groundAtoms, weights, context.var2expr, context.var2term);
        context.selectBuilder.id("var" + index++).expr(expression);
      }

      context.selectBuilder.tupleForIds();

      //add the weight table
      factorFormula.getWeight().acceptTermVisitor(new AbstractTermVisitor() {
        public void visitFunctionApplication(FunctionApplication functionApplication) {
          functionApplication.getFunction().acceptFunctionVisitor(new AbstractFunctionVisitor() {
            public void visitWeightFunction(WeightFunction weightFunction) {
              context.prefixes.add("weights");
              context.relations.add(weights.getRelation(weightFunction));
            }
          });
        }
      });


    }
  }

  public RelationExpression generateLocalCollectorQuery(FactorFormula factorFormula,
                                                        GroundAtoms observation,
                                                        Weights w) {
    if (!factorFormula.isLocal()) throw new IllegalArgumentException("Factor formula must be local for " +
            "generating a local feature query");

    this.groundAtoms = observation;
    this.weights = w;

    builder = new FormulaBuilder(observation.getSignature());

    Conjunction both = new Conjunction(factorFormula.getCondition(),factorFormula.getFormula());

    DNF dnf = dnfGenerator.convertToDNF(both);
    conjunctions = new LinkedList<ConjunctionProcessor.Context>();

    ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);

    FunctionApplication weight = (FunctionApplication) factorFormula.getWeight();
    WeightFunction function = (WeightFunction) weight.getFunction();

    for (List<SignedAtom> conjunction : dnf.getConjunctions()) {
      //create conjunction context
      ConjunctionProcessor.Context conjunctionContext = new ConjunctionProcessor.Context();
      conjunctions.add(conjunctionContext);
      //process the condition conjunction
      conjunctionProcessor.processConjunction(conjunctionContext, conjunction);
      //now process the weight part.
      TermResolver resolver = new TermResolver();
      NoDExpressionGenerator generator = new NoDExpressionGenerator();
      int argIndex = 0;
      for (Term term : weight.getArguments()){
        Term resolved = resolver.resolve(term, conjunctionContext.var2term);
        if (resolver.getUnresolved().size() > 0)
          throw new RuntimeException("During collection all terms in the weight function application must be bound");
        Expression expr = generator.convertTerm(resolved, groundAtoms, weights,
                conjunctionContext.var2expr, conjunctionContext.var2term);
        conjunctionContext.selectBuilder.id(function.getColumnName(argIndex++)).expr(expr);
      }
      conjunctionContext.selectBuilder.id("index").num(0);
      //make a tuple using all added columns
      conjunctionContext.selectBuilder.tuple();
    }

    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    if (conjunctions.size() == 1) {
      ConjunctionProcessor.Context context = conjunctions.get(0);
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);

  }

  public RelationExpression generateLocalQuery(FactorFormula factorFormula, GroundAtoms observation, Weights w) {
    if (!factorFormula.isLocal()) throw new IllegalArgumentException("Factor formula must be local for " +
            "generating a local feature query");

    this.groundAtoms = observation;
    this.weights = w;

    builder = new FormulaBuilder(observation.getSignature());

    DNF dnf = dnfGenerator.convertToDNF(factorFormula.getCondition());
    conjunctions = new LinkedList<ConjunctionProcessor.Context>();

    ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);

    for (List<SignedAtom> conjunction : dnf.getConjunctions()) {
      //create conjunction context
      ConjunctionProcessor.Context conjunctionContext = new ConjunctionProcessor.Context();
      conjunctions.add(conjunctionContext);
      //process the condition conjunction
      conjunctionProcessor.processConjunction(conjunctionContext, conjunction);
      //processConjunction(conjunctionContext, conjunction);
      //process the single hidden atom
      processHiddenAtom(conjunctionContext, (Atom) factorFormula.getFormula());
      //now process the weight part.
      processWeightForLocal(conjunctionContext, factorFormula.getWeight());
      //make a tuple using all added columns
      conjunctionContext.selectBuilder.tuple();
    }

    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    if (conjunctions.size() == 1) {
      ConjunctionProcessor.Context context = conjunctions.get(0);
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);

  }


  private void processWeightForLocal(ConjunctionProcessor.Context context, Term weight) {
    if (!(weight instanceof FunctionApplication))
      throw new RuntimeException("Weight term must be the application of a weight function but it's not even a " +
              "function application");
    FunctionApplication weightOfArg = (FunctionApplication) weight;
    if (!(weightOfArg.getFunction() instanceof WeightFunction))
      throw new RuntimeException("Weight term must be the application of a weight function but in this case it's " +
              "a different type of function");

    String prefix = "weights";

    processWeightArgs(weightOfArg, context, prefix);

    WeightFunction weightFunction = (WeightFunction) weightOfArg.getFunction();
    //process the arguments of the hidden atom which were unbound
    for (Map.Entry<String, Term> entry : context.remainingHiddenArgs.entrySet()) {
      Term resolved = termResolver.resolve(entry.getValue(), context.var2term);
      if (!termResolver.allResolved())
        throw new RuntimeException("Arguments of the hidden atom must all be bound but " + entry.getValue() +
                " is not");
      Expression expr = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, context.var2term);
      context.selectBuilder.id(entry.getKey()).expr(expr);
    }
    context.selectBuilder.id("index").attribute(prefix, weightFunction.getIndexAttribute());
    //context.selectBuilder.id("score").doubleValue(0.0);
  }

  private void processWeightForGlobal(final ConjunctionProcessor.Context context, Term weight) {
    weight.acceptTermVisitor(new TermVisitor() {
      public void visitVariable(Variable variable) {

      }

      public void visitFunctionApplication(final FunctionApplication functionApplication) {
        functionApplication.getFunction().acceptFunctionVisitor(new FunctionVisitor() {
          public void visitWeightFunction(WeightFunction weightFunction) {
            String prefix = "weights";

            processWeightArgsForGlobal(functionApplication, context, prefix);

            context.selectBuilder.id("index").attribute(prefix, weightFunction.getIndexAttribute());

          }

          public void visitIntAdd(thebeast.pml.function.IntAdd intAdd) {

          }

          public void visitIntMinus(thebeast.pml.function.IntMinus intMinus) {

          }
        });
      }

      public void visitIntConstant(IntConstant intConstant) {

      }

      public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {

      }

      public void visitDontCare(DontCare dontCare) {

      }

      public void visitDoubleConstant(DoubleConstant doubleConstant) {
        context.selectBuilder.doubleValue(doubleConstant.getValue());
      }

      public void visitBinnedInt(BinnedInt binnedInt) {
        
      }
    });

//    if (!(weight instanceof FunctionApplication))
//      throw new RuntimeException("Weight term must be the application of a weight function but it's not even a " +
//              "function application");
//    FunctionApplication weightOfArg = (FunctionApplication) weight;
//    if (!(weightOfArg.getFunction() instanceof WeightFunction))
//      throw new RuntimeException("Weight term must be the application of a weight function but in this case it's " +
//              "a different type of function");
//
//    String prefix = "weights";
//
//    processWeightArgsForGlobal(weightOfArg, context, prefix);
//
//    WeightFunction weightFunction = (WeightFunction) weightOfArg.getFunction();
//
//    context.selectBuilder.id("index").attribute(prefix, weightFunction.getIndexAttribute());
//    //context.selectBuilder.id("score").doubleValue(0.0);
  }

  private void processWeightArgs(FunctionApplication weightOfArg, ConjunctionProcessor.Context context, String prefix) {
    WeightFunction weightFunction = (WeightFunction) weightOfArg.getFunction();
    context.prefixes.add(prefix);
    context.relations.add(weights.getRelation(weightFunction));
    int argIndex = 0;

    for (Term arg : weightOfArg.getArguments()) {
      Term resolved = termResolver.resolve(arg, context.var2term);
      //if there is more than one unbound variables we leave things as they are.
      if (termResolver.allResolved()) {
        String varName = prefix + "_" + weightFunction.getColumnName(argIndex);
        Variable artificial = new Variable(arg.getType(), varName);
        context.var2expr.put(artificial, factory.createAttribute(prefix, weightFunction.getAttributeForArg(argIndex)));
        builder.var(artificial).term(resolved).equality();
        context.conditions.add((BoolExpression) exprGenerator.convertFormula(
                builder.getFormula(), groundAtoms, weights, context.var2expr, context.var2term));

      } else if (termResolver.getUnresolved().size() == 1) {
        String varName = prefix + "_" + weightFunction.getColumnName(argIndex);
        Variable artificial = new Variable(arg.getType(), varName);
        context.var2expr.put(artificial, factory.createAttribute(prefix, weightFunction.getAttributeForArg(argIndex)));
        Variable toResolve = termResolver.getUnresolved().get(0);
        Term inverted = inverter.invert(resolved, artificial, toResolve);
        context.var2term.put(toResolve, inverted);
      }
      ++argIndex;
    }
  }

  private void processWeightArgsForGlobal(FunctionApplication weightOfArg,
                                          ConjunctionProcessor.Context context, String prefix) {
    WeightFunction weightFunction = (WeightFunction) weightOfArg.getFunction();
    //context.prefixes.add(prefix);
    //context.relations.add(weights.getRelation(weightFunction));
    int argIndex = 0;

    for (Term arg : weightOfArg.getArguments()) {
      Term resolved = termResolver.resolve(arg, context.var2term);
      //if there is more than one unbound variable we leave things as they are.
      if (termResolver.getUnresolved().size() == 0) {

      }
      if (termResolver.getUnresolved().size() == 1) {
        String varName = prefix + "_" + weightFunction.getColumnName(argIndex);
        Variable artificial = new Variable(arg.getType(), varName);
        context.var2expr.put(artificial, factory.createAttribute(prefix, weightFunction.getAttributeForArg(argIndex)));
        Variable toResolve = termResolver.getUnresolved().get(0);
        Term inverted = inverter.invert(resolved, artificial, toResolve);
        context.var2term.put(toResolve, inverted);
      }
      ++argIndex;
    }
  }


  private void processHiddenAtom(final ConjunctionProcessor.Context context, Atom atom) {
    atom.acceptAtomVisitor(new AtomVisitor() {
      public void visitPredicateAtom(PredicateAtom predicateAtom) {
        int argIndex = 0;
        for (Term arg : predicateAtom.getArguments()) {
          String attributeName = ((UserPredicate) predicateAtom.getPredicate()).getColumnName(argIndex++);
          Term resolved = termResolver.resolve(arg, context.var2term);
          //if not every variable was resolved we leave things as they are and hope for the weight function
          if (!termResolver.allResolved()) {
            context.remainingHiddenArgs.put(attributeName, resolved);
          } else {
            //to add the remaining hidden arguments to the select tuple
            Expression expr = exprGenerator.convertTerm(
                    resolved, groundAtoms, weights, context.var2expr, context.var2term);
            context.selectBuilder.id(attributeName).expr(expr);
          }
        }

      }

      public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {

      }
    });
  }


  public RelationExpression generateConstraintQuery(FactorFormula formula, GroundFormulas groundFormulas,
                                                    final Scores scores, final IntegerLinearProgram ilp,
                                                    final Model model) {
    this.scores = scores;
    this.groundFormulas = groundFormulas;
    this.ilp = ilp;
    this.formula = formula;

    if (formula.getFormula() instanceof AcyclicityConstraint) {
      return generateCycleConstraints((AcyclicityConstraint) formula.getFormula());
    }

    //we only need to consider the this.formula (not the condition)
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
    constraintBuilder.expr(this.groundFormulas.getExplicitGroundFormulas(this.formula)).from("formulas");
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
      signedAtom.getAtom().acceptAtomVisitor(new AtomVisitor() {
        public void visitPredicateAtom(final PredicateAtom predicateAtom) {
          signsWithoutCardinalities.add(signedAtom.isTrue());
          predicateAtom.getPredicate().acceptPredicateVisitor(new AbstractPredicateVisitor() {
            public void visitUserPredicate(UserPredicate userPredicate) {
              ExpressionBuilder weightBuilder = varBuilder.createBuilder();
              weightBuilder.expr(QueryGenerator.this.scores.getScoreRelation(userPredicate));
              varBuilder.expr(QueryGenerator.this.ilp.getGroundAtomIndices(userPredicate));
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
              varBuilder.id("index").expr(QueryGenerator.this.ilp.getVarCount()).intPostInc();
              varBuilder.id("score").expr(weightBuilder.getExpression());
              varBuilder.tuple(2);
              varBuilder.getPut().intExtractComponent("index");
            }
          });
        }

        public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
          if (!signedAtom.isTrue())
            throw new RuntimeException("Cardinality constraints must be non-negated");
          if (atoms.size() > 1 && useDisjunction && !cardinalityConstraint.isGEQ() ||
                  atoms.size() > 1 && !useDisjunction && !cardinalityConstraint.isLEQ())
            throw new RuntimeException("Sorry, we can't do LEQ constraints in disjunctions or" +
                    "GEQ constraints in conjunctions yet");
          //if the formula is deterministic we can actually spell out the disjunction
          if (!cardinalityConstraint.isGEQ() && !cardinalityConstraint.isLEQ()) {
            throw new RuntimeException("Sorry, we can't do cardinality constraints with lower and upper bound" +
                    "yet");
          }
          if (cardinalityConstraint.isLEQ()) {
            leqQueries.add(createLEQQuery(cardinalityConstraint, var2expr, model));
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
      });
    }
    //call the constraint operator to generate constraints.
    if (!this.formula.isDeterministic()) {
      constraintBuilder.expressions(varBuilder.lastExpressions(atoms.size() + 1));
      constraintBuilder.invokeRelOp(generateSoftConstraintOperator(useDisjunction, signs));
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
    weightBuilder.expr(QueryGenerator.this.scores.getScoreRelation(predicate));
    cycleBuilder.id("index");
    cycleBuilder.expr(QueryGenerator.this.ilp.getGroundAtomIndices(predicate));

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
    cycleBuilder.id("index").expr(QueryGenerator.this.ilp.getVarCount()).intPostInc();
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

  public RelationExpression generateCycleQuery(GroundAtoms groundAtoms, AcyclicityConstraint acyclicityConstraint) {
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    UserPredicate predicate = acyclicityConstraint.getPredicate();
    builder.expr(groundAtoms.getGroundAtomsOf(predicate).getRelationVariable());
    builder.cycles(predicate.getColumnName(0), predicate.getColumnName(1));
    return builder.getRelation();
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

    processor.processConjunction(context, conjunction);

    ArrayList<SignedAtom> hiddenList = new ArrayList<SignedAtom>();
    hiddenList.add(hidden);
    processor.resolveBruteForce(context, hiddenList);

    //find the hidden ground atom
    context.selectBuilder.id("index");

    hidden.getAtom().acceptAtomVisitor(new AbstractAtomVisitor() {
      public void visitPredicateAtom(final PredicateAtom predicateAtom) {
        predicateAtom.getPredicate().acceptPredicateVisitor(new AbstractPredicateVisitor() {
          public void visitUserPredicate(UserPredicate userPredicate) {
            if (model.getHiddenPredicates().contains(userPredicate)) {
              ExpressionBuilder weightBuilder = context.selectBuilder.createBuilder();
              weightBuilder.expr(QueryGenerator.this.scores.getScoreRelation(userPredicate));
              context.selectBuilder.expr(QueryGenerator.this.ilp.getGroundAtomIndices(userPredicate));
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
              context.selectBuilder.id("index").expr(QueryGenerator.this.ilp.getVarCount()).intPostInc();
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

  public RelationExpression createLEQQuery(CardinalityConstraint constraint,
                                           Map<Variable, Expression> var2expr,
                                           final Model model) {
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

    processor.processConjunction(context, conjunction);

    //find the hidden ground atom
    context.selectBuilder.id("index");
    for (SignedAtom atom : conjunction)
      atom.getAtom().acceptAtomVisitor(new AbstractAtomVisitor() {
        public void visitPredicateAtom(final PredicateAtom predicateAtom) {
          predicateAtom.getPredicate().acceptPredicateVisitor(new AbstractPredicateVisitor() {
            public void visitUserPredicate(UserPredicate userPredicate) {
              if (model.getHiddenPredicates().contains(userPredicate)) {
                ExpressionBuilder weightBuilder = context.selectBuilder.createBuilder();
                weightBuilder.expr(QueryGenerator.this.scores.getScoreRelation(userPredicate));
                context.selectBuilder.expr(QueryGenerator.this.ilp.getGroundAtomIndices(userPredicate));
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
                context.selectBuilder.id("index").expr(QueryGenerator.this.ilp.getVarCount()).intPostInc();
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
    builder.id("lb").doubleValue(disjunction ? -falseCount : size - falseCount);
    builder.id("ub").num(Double.POSITIVE_INFINITY);
    builder.id("values");
    builder.id("index").expr(f).id("weight").num(-1.0).tuple(2);
    for (int i = 0; i < size; ++i) {
      builder.id("index").expr(variables[i]).id("weight").num(signs[i] ? 1.0 : -1.0).tuple(2);
    }
    builder.relation(size + 1);
    builder.tuple(3);
    //the 'small' constraints
    double scale = disjunction ? 1.0 : -1.0;
    for (int i = 0; i < size; ++i) {
      builder.id("lb").num(0.0);
      builder.id("ub").num(Double.POSITIVE_INFINITY);
      builder.id("values");
      builder.id("index").expr(f).id("weight").num(scale).tuple(2);
      builder.id("index").expr(variables[i]).id("weight").num(-scale).tuple(2);
      builder.relation(2);
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
    builder.relation(size);
    builder.tuple(3);
    builder.relation(1);
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
      builder.id("index").expr(variables.get(i+2));
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
      builder.id("index").expr(variables.get(i+2));
      builder.id("weight").expr(upperBound).expr(items).count().doubleCast().doubleAdd();
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


}
