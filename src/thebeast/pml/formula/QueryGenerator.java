package thebeast.pml.formula;

import thebeast.nod.expression.*;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.*;
import thebeast.pml.*;
import thebeast.pml.solve.ilp.IntegerLinearProgram;
import thebeast.pml.function.*;
import thebeast.pml.function.DoubleCast;
import thebeast.pml.function.DoubleAbs;
import thebeast.pml.function.DoubleAdd;
import thebeast.pml.function.DoubleMinus;
import thebeast.pml.term.*;
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
  private GroundAtoms closure;
  private Model model;

  public QueryGenerator(Model model) {
    factory = TheBeast.getInstance().getNodServer().expressionFactory();
    this.model = model;
  }

  public QueryGenerator(Model model, Weights weights, GroundAtoms groundAtoms) {
    factory = TheBeast.getInstance().getNodServer().expressionFactory();
    this.weights = weights;
    this.groundAtoms = groundAtoms;
    this.model = model;
  }


  public void setClosure(GroundAtoms closure) {
    this.closure = closure;
  }

  public RelationExpression generateGlobalAllQuery(FactorFormula factorFormula, GroundAtoms groundAtoms, Weights w) {
    this.groundAtoms = groundAtoms;
    this.weights = w;
    builder = new FormulaBuilder(groundAtoms.getSignature());

    BooleanFormula condition = factorFormula.getCondition();

    if (condition == null) condition = new True();

    processGlobalFormula(condition, factorFormula,false);
    //if there is just one conjunction we don't need a union.                           
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    for (ConjunctionProcessor.Context context : conjunctions) {
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);
  }


  public RelationExpression generateGlobalTrueQuery(FactorFormula factorFormula, GroundAtoms groundAtoms, Weights w) {
    this.groundAtoms = groundAtoms;
    this.weights = w;
    builder = new FormulaBuilder(groundAtoms.getSignature());

    BooleanFormula condition = factorFormula.getCondition();
    BooleanFormula both = condition == null ?
            factorFormula.getFormula() : new Conjunction(factorFormula.getFormula(), condition);
    processGlobalFormula(both, factorFormula,false);
    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    for (ConjunctionProcessor.Context context : conjunctions) {
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);
  }

  public RelationExpression generateAuxiliaryQuery(FactorFormula factorFormula, GroundAtoms groundAtoms, Weights w) {
    this.groundAtoms = groundAtoms;
    this.weights = w;
    builder = new FormulaBuilder(groundAtoms.getSignature());

    PredicateAtom atom;
    BooleanFormula both;
    if (factorFormula.getFormula() instanceof Implication) {
      Implication implication = (Implication) factorFormula.getFormula();
      atom = (PredicateAtom) implication.getConclusion();

      BooleanFormula condition = factorFormula.getCondition();
      both = condition == null ?
              implication.getPremise() : new Conjunction(implication.getPremise(), condition);
    } else if (factorFormula.getFormula() instanceof PredicateAtom) {
      atom = (PredicateAtom) factorFormula.getFormula();
      both = factorFormula.getCondition();
    } else {
      throw new RuntimeException(factorFormula + " is not a valid auxilary generator");
    }
    UserPredicate predicate = (UserPredicate) atom.getPredicate();
    DNF dnf = DNFGenerator.generateDNF(both);
    conjunctions = new LinkedList<ConjunctionProcessor.Context>();
    ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);


    for (List<SignedAtom> conjunction : dnf.getConjunctions()) {

      //create conjunction context
      final ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
      conjunctions.add(context);

//      //we process the weights
//      processWeightForGlobal(context, factorFormula.getWeight());

      //process the condition conjunction
      conjunctionProcessor.processConjunction(context, conjunction);

      //now add the auxilary atom argument terms to the select statement
      for (int i = 0; i < predicate.getArity(); ++i) {
        Term term = termResolver.resolve(atom.getArguments().get(i), context.var2term);
        Expression expression = exprGenerator.convertTerm(term,
                groundAtoms, weights, context.var2expr, context.var2term);

        context.selectBuilder.id(predicate.getColumnName(i)).expr(expression);
      }
      context.selectBuilder.tupleForIds();
    }

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
    //todo: to decide whether condition or formula comes first we need to see if we can use the formula to bind free variables.
    BooleanFormula both = condition == null ?
            negated : factorFormula.getFormula() instanceof CardinalityConstraint ?
            new Conjunction(condition, negated) : new Conjunction(negated, condition);
    processGlobalFormula(both, factorFormula,false);
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

    BooleanFormula condition = factorFormula.getCondition();

    BooleanFormula both = condition == null ?
            factorFormula.getFormula() : new Conjunction(condition, factorFormula.getFormula());
    processGlobalFormula(both, factorFormula,true);

    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    for (ConjunctionProcessor.Context context : conjunctions) {
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);
  }


  private void divideToHiddenAndObserved(List<SignedAtom> atoms, List<SignedAtom> hidden,
                                         List<SignedAtom> observed){
    for (SignedAtom atom : atoms){
      if (model.isHidden(atom.getAtom())) hidden.add(atom);
      else observed.add(atom);
    }
  }
  
  private void processGlobalFormula(BooleanFormula both, FactorFormula factorFormula, boolean observedFirst) {
    DNF dnf = DNFGenerator.generateDNF(both);
    conjunctions = new LinkedList<ConjunctionProcessor.Context>();
    ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);

    for (List<SignedAtom> conjunction : dnf.getConjunctions()) {

      //create conjunction context
      final ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
      conjunctions.add(context);

      //we process the weights
      if (!observedFirst) {
//        List<SignedAtom> hidden = new ArrayList<SignedAtom>();
//        List<SignedAtom> observed = new ArrayList<SignedAtom>();
//        divideToHiddenAndObserved(conjunction, hidden, observed);
//        conjunctionProcessor.processConjunction(context, hidden);
//        conjunctionProcessor.processConjunction(context, observed);
//        processWeightForGlobal(context, factorFormula.getWeight());
//        processScale(factorFormula.getWeight(), context);
//
//        //TODO: this should rather be: 1. Condition 2. Weight 3. Formula
        processWeightForGlobal(context, factorFormula.getWeight());
        //process the condition conjunction
        conjunctionProcessor.processConjunction(context, conjunction);
        processScale(factorFormula.getWeight(), context);
      } else {
        List<SignedAtom> hidden = new ArrayList<SignedAtom>();
        List<SignedAtom> observed = new ArrayList<SignedAtom>();
        divideToHiddenAndObserved(conjunction, hidden, observed);
        conjunctionProcessor.processConjunction(context, observed);
        processScale(factorFormula.getWeight(), context);
        processWeightForGlobal(context, factorFormula.getWeight());
        conjunctionProcessor.processConjunction(context, hidden);
      }

      //process the variables unresolved in the weight
      //processRemainingUnresolved(context);

      //now add the quantification variables as attributes
      selectVariables(factorFormula.getQuantification().getVariables(), context, factorFormula);

      context.selectBuilder.tupleForIds();

      //add the weight table
      factorFormula.getWeight().acceptTermVisitor(new GlobalFactorWeightProcessor(context));
    }
  }

  private void selectVariables(List<Variable> variables, ConjunctionProcessor.Context context, FactorFormula factorFormula) {
    int index = 0;
    for (Variable var : variables) {
      Term term = context.var2term.get(var);
      if (term == null)
        if (var.getType().isNumeric()) throw new RuntimeException(var + " is unbound in " + factorFormula);
        else {
          exprBuilder.allConstants((CategoricalType) var.getType().getNodType());
          RelationExpression allConstants = exprBuilder.getRelation();
          String prefix = var.getName();
          context.selectBuilder.expr(allConstants).from(prefix);
          context.prefixes.add(prefix);
          context.relations.add(allConstants);
          context.selectBuilder.id("var" + index++).categoricalAttribute(prefix, "value");
        }
      else {
        Expression expression = exprGenerator.convertTerm(term, groundAtoms, weights, context.var2expr, context.var2term);
        context.selectBuilder.id("var" + index++).expr(expression);
      }
    }
  }

  public RelationExpression generateCollectorQuery(FactorFormula factorFormula,
                                                   GroundAtoms observation,
                                                   Weights w) {
    return generateCollectorQuery(factorFormula, observation, false, w);

  }
  public RelationExpression generateCollectorQuery(FactorFormula factorFormula,
                                                   GroundAtoms observation,
                                                   boolean negatives,
                                                   Weights w) {
    this.groundAtoms = observation;
    this.weights = w;

    builder = new FormulaBuilder(observation.getSignature());

    BooleanFormula formula;
    BooleanFormula condition;
    if (factorFormula.isLocal()) {
      formula = factorFormula.getFormula();
      condition = factorFormula.getCondition();
    } else if (factorFormula.getFormula() instanceof Implication) {
      Implication implication = (Implication) factorFormula.getFormula();
      formula = implication.getConclusion();
      condition = factorFormula.getCondition() == null ?
              implication.getPremise() : new Conjunction(factorFormula.getCondition(),implication.getPremise());

    } else if (factorFormula.getFormula() instanceof Conjunction) {
      formula = factorFormula.getFormula();
      condition = factorFormula.getCondition();
    } else if (factorFormula.getFormula() instanceof Disjunction) {
      formula = factorFormula.getFormula();
      condition = factorFormula.getCondition();
    } else {
      return null;
    }

    BooleanFormula queryFormula = condition == null ?
            formula :
            negatives ? condition : new Conjunction(formula, condition);
    //Conjunction queryFormula = new Conjunction(factorFormula.getFormula(), factorFormula.getCondition());

    DNF dnf = dnfGenerator.convertToDNF(queryFormula);
    conjunctions = new LinkedList<ConjunctionProcessor.Context>();

    ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);

    FunctionApplication weight = (FunctionApplication) factorFormula.getWeight();
    if (weight.getFunction() instanceof DoubleProduct){
      weight = (FunctionApplication) weight.getArguments().get(1);
    }
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
      for (Term term : weight.getArguments()) {
        Term resolved = resolver.resolve(term, conjunctionContext.var2term);
        if (resolver.getUnresolved().size() > 0)
          throw new RuntimeException("During collection all terms in the weight function application must be bound" +
                  " but this is not the case for " + factorFormula);
        Expression expr = generator.convertTerm(resolved, groundAtoms, weights,
                conjunctionContext.var2expr, conjunctionContext.var2term);
        conjunctionContext.selectBuilder.id(function.getColumnName(argIndex++)).expr(expr);
      }
      conjunctionContext.selectBuilder.id("index").num(0);
      conjunctionContext.selectBuilder.id("count").num(1);
      //make a tuple using all added columns
      conjunctionContext.selectBuilder.tuple();
    }

    //if there is just one conjunction we don't need a union.
    LinkedList<RelationExpression> rels = new LinkedList<RelationExpression>();
    if (conjunctions.size() == 1) {
      ConjunctionProcessor.Context context = conjunctions.get(0);
      BoolExpression where = factory.createAnd(context.conditions);
      rels.add(factory.createQuery(context.prefixes, context.relations,
              where, context.selectBuilder.getTuple(), false));
    }
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);

  }

  public RelationExpression generateLocalQuery(FactorFormula factorFormula, GroundAtoms observation, Weights w) {
    if (!factorFormula.isLocal()) throw new IllegalArgumentException("Factor formula must be local for " +
            "generating a local feature query");

    this.groundAtoms = observation;
    this.weights = w;

    builder = new FormulaBuilder(observation.getSignature());

    conjunctions = new LinkedList<ConjunctionProcessor.Context>();

    if (factorFormula.getCondition() != null) {
      DNF dnf = dnfGenerator.convertToDNF(factorFormula.getCondition());

      ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);

      for (List<SignedAtom> conjunction : dnf.getConjunctions()) {
        //create conjunction context
        ConjunctionProcessor.Context conjunctionContext = new ConjunctionProcessor.Context();
        conjunctions.add(conjunctionContext);
        //process the condition conjunction
        conjunctionProcessor.processConjunction(conjunctionContext, conjunction, false);
        //processConjunction(conjunctionContext, conjunction);
        //process the single hidden atom
        processHiddenAtom(conjunctionContext, (Atom) factorFormula.getFormula());
        //now process the weight part.
        processWeightForLocal(conjunctionContext, factorFormula.getWeight());
        //now process what we couldn't process so far
        conjunctionProcessor.processConjunction(conjunctionContext, conjunctionContext.remainingAtoms, true);
        //make a tuple using all added columns
        conjunctionContext.selectBuilder.tuple();
      }
    } else {
      ConjunctionProcessor.Context conjunctionContext = new ConjunctionProcessor.Context();
      conjunctions.add(conjunctionContext);
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
    //System.out.println(rels.get(0));
    return rels.size() == 1 ? rels.get(0) : factory.createUnion(rels);


  }

  public RelationExpression generateDirectLocalScoreQuery(FactorFormula factorFormula,
                                                          IntVariable index,
                                                          GroundAtoms observation,
                                                          Weights w) {
    if (!factorFormula.isLocal()) throw new IllegalArgumentException("Factor formula must be local for " +
            "generating a local feature query");

    this.groundAtoms = observation;
    this.weights = w;

    builder = new FormulaBuilder(observation.getSignature());

    conjunctions = new LinkedList<ConjunctionProcessor.Context>();

    if (factorFormula.getCondition() != null) {
      DNF dnf = dnfGenerator.convertToDNF(factorFormula.getCondition());

      ConjunctionProcessor conjunctionProcessor = new ConjunctionProcessor(weights, groundAtoms);

      for (List<SignedAtom> conjunction : dnf.getConjunctions()) {
        //create conjunction context
        ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
        conjunctions.add(context);
        //process the condition conjunction
        conjunctionProcessor.processConjunction(context, conjunction);
        //processConjunction(conjunctionContext, conjunction);
        conjunctionProcessor.resolveBruteForce(context, (Atom) factorFormula.getFormula());
        //process the single hidden atom
        processHiddenAtom(context, (Atom) factorFormula.getFormula());
        //now process the score term
        Term term = termResolver.resolve(factorFormula.getWeight(), context.var2term);
        Expression expr = exprGenerator.convertTerm(term, groundAtoms, weights, context.var2expr, context.var2term);
        context.selectBuilder.id("score").expr(expr);
        context.selectBuilder.id("index").expr(index).intPostInc();
        //make a tuple using all added columns
        context.selectBuilder.tuple();
      }
    } else {
      ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
      conjunctions.add(context);
      processHiddenAtom(context, (Atom) factorFormula.getFormula());
      //now process the weight part.
      //now process the score term
      Term term = termResolver.resolve(factorFormula.getWeight(), context.var2term);
      Expression expr = exprGenerator.convertTerm(term, groundAtoms, weights, context.var2expr, context.var2term);
      context.selectBuilder.id("score").expr(expr);
      context.selectBuilder.id("index").expr(index).intPostInc();
      //make a tuple using all added columns
      context.selectBuilder.tuple();
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


  //binds/makes consistent arguments of the weight function and adds the index of the local feature to
  //the result table
  //TODO: add scaling column
  private void processWeightForLocal(ConjunctionProcessor.Context context, Term weight) {

    if (!(weight instanceof FunctionApplication))
      throw new RuntimeException("Weight term must be the application of a weight function but it's not even a " +
              "function application");
    FunctionApplication application = (FunctionApplication) weight;

    //if a product s * w(a_i,...) 
    if (application.getFunction() instanceof DoubleProduct) {
      Term scale = application.getArguments().get(0);
      Term resolved = termResolver.resolve(scale, context.var2term);
      if (!termResolver.allResolved())
        throw new RuntimeException(termResolver.getUnresolved().toString() + " can't be resolved in " + scale);
      Expression expr = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, context.var2term);
      context.selectBuilder.id("scale").expr(expr);

      application = (FunctionApplication) application.getArguments().get(1);
    } else if (application.getFunction() instanceof WeightFunction)
      context.selectBuilder.id("scale").doubleValue(1.0);
    else
      throw new RuntimeException("Weight term must be the application of a weight function but in this case it's " +
              "a different type of function");

    String prefix = "weights";

    processWeightArgs(application, context, prefix);

    WeightFunction weightFunction = (WeightFunction) application.getFunction();
    //process the arguments of the hidden atom which were unbound
    processRemainingUnresolved(context);
    context.selectBuilder.id("index").attribute(prefix, weightFunction.getIndexAttribute());
    //context.selectBuilder.id("score").doubleValue(0.0);
  }


  private void processRemainingUnresolved(ConjunctionProcessor.Context context) {
    for (Map.Entry<String, Term> entry : context.remainingHiddenArgs.entrySet()) {
      Term term = entry.getValue();
      Term resolved = termResolver.resolve(term, context.var2term);
      if (!termResolver.allResolved()) {
        if (term.getType().getTypeClass() != Type.Class.CATEGORICAL &&
                term.getType().getTypeClass() != Type.Class.CATEGORICAL_UNKNOWN)
          throw new RuntimeException("Arguments of the hidden atom must all be bound but " + term +
                  " is not");
        else {
          String prefix = "all_" + term.toString();
          RelationExpression allConstants = exprBuilder.allConstants((CategoricalType) term.getType().getNodType()).getRelation();
          context.prefixes.add(prefix);
          context.relations.add(allConstants);
          context.selectBuilder.expr(allConstants);
          context.selectBuilder.from(prefix);
          context.selectBuilder.id(entry.getKey()).categoricalAttribute(prefix, "value");
        }
      } else {
        Expression expr = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, context.var2term);
        context.selectBuilder.id(entry.getKey()).expr(expr);
      }
    }
  }

  private void processScale(Term weight, ConjunctionProcessor.Context context) {
    if (weight instanceof DoubleConstant) {
      context.selectBuilder.doubleValue(((DoubleConstant) weight).getValue());
      return;
    }
    FunctionApplication app = (FunctionApplication) weight;
    if (app.getFunction() instanceof DoubleProduct) {
      Term scale = app.getArguments().get(0);
      Term resolved = termResolver.resolve(scale, context.var2term);
      if (!termResolver.allResolved())
        throw new RuntimeException(termResolver.getUnresolved().toString() + " can't be resolved in " + scale);
      Expression expr = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, context.var2term);
      context.selectBuilder.id("scale").expr(expr);
      app = (FunctionApplication) app.getArguments().get(1);
    } else {
      context.selectBuilder.id("scale").num(1.0);
    }
  }


  private void processWeightForGlobal(final ConjunctionProcessor.Context context, Term weight) {
    if (weight instanceof DoubleConstant) return;
//      context.selectBuilder.doubleValue(((DoubleConstant) weight).getValue());
//      return;
//    }
    FunctionApplication app = (FunctionApplication) weight;
    if (app.getFunction() instanceof DoubleProduct) {
//      Term scale = app.getArguments().get(0);
//      Term resolved = termResolver.resolve(scale, context.var2term);
//      if (!termResolver.allResolved())
//        throw new RuntimeException(termResolver.getUnresolved().toString() + " can't be resolved in " + scale);
//      Expression expr = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, context.var2term);
//      context.selectBuilder.id("scale").expr(expr);
      app = (FunctionApplication) app.getArguments().get(1);
    }
//    else {
//      context.selectBuilder.id("scale").num(1.0);
//    }
    WeightFunction weightFunction = (WeightFunction) app.getFunction();
    String prefix = "weights";

    processWeightArgsForGlobal(app, context, prefix);

    context.selectBuilder.id("index").attribute(prefix, weightFunction.getIndexAttribute());

  }

  // Processes the argument terms of the function application f(t_1,t_2,...) and either binds t_i -> f_column_i if
  // t_i has not yet been bound or adds the condition f_column_i == resolve(t_i) if t_i has been bound.
  // If t_i contains several unresolvable variables we leave things as they are. 
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

  //binds unbound terms in the weight function application to columns of the weight function table
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
        String varName = prefix + "_" + weightFunction.getColumnName(argIndex);
        Variable artificial = new Variable(arg.getType(), varName);
        context.var2expr.put(artificial, factory.createAttribute(prefix, weightFunction.getAttributeForArg(argIndex)));
        builder.var(artificial).term(resolved).equality();
        context.conditions.add((BoolExpression) exprGenerator.convertFormula(
                builder.getFormula(), groundAtoms, weights, context.var2expr, context.var2term));
        //throw new RuntimeException("No unresolved variable for " + arg + " in " + weightOfArg);
      }
      else if (termResolver.getUnresolved().size() == 1) {
        String varName = prefix + "_" + weightFunction.getColumnName(argIndex);
        Variable artificial = new Variable(arg.getType(), varName);
        context.var2expr.put(artificial, factory.createAttribute(prefix, weightFunction.getAttributeForArg(argIndex)));
        Variable toResolve = termResolver.getUnresolved().get(0);
        Term inverted = inverter.invert(resolved, artificial, toResolve);
        if (inverted.equals(resolved)) {
          context.remainingHiddenArgs.put(weightFunction.getColumnName(argIndex), resolved);
        } else
          context.var2term.put(toResolve, inverted);
      } else {
        context.remainingHiddenArgs.put(weightFunction.getColumnName(argIndex), resolved);
      }
      ++argIndex;
    }
  }


  private void processHiddenAtom(final ConjunctionProcessor.Context context, Atom atom) {
    atom.acceptAtomVisitor(new AbstractAtomVisitor() {
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

      public void visitTrue(True aTrue) {

      }
    });
  }


  public RelationExpression generateCycleQuery(GroundAtoms groundAtoms, AcyclicityConstraint acyclicityConstraint) {
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    UserPredicate predicate = acyclicityConstraint.getPredicate();
    builder.expr(groundAtoms.getGroundAtomsOf(predicate).getRelationVariable());
    builder.cycles(predicate.getColumnName(acyclicityConstraint.getFrom()),
            predicate.getColumnName(acyclicityConstraint.getTo()));
    return builder.getRelation();
  }


  public void setScores(Scores scores) {
    this.scores = scores;
  }

  private class GlobalFactorWeightProcessor extends AbstractTermVisitor {
    private final ConjunctionProcessor.Context context;

    public GlobalFactorWeightProcessor(ConjunctionProcessor.Context context) {
      this.context = context;
    }

    public void visitFunctionApplication(final FunctionApplication functionApplication) {
      functionApplication.getFunction().acceptFunctionVisitor(new AbstractFunctionVisitor() {

        public void visitDoubleProduct(DoubleProduct doubleProduct) {
          functionApplication.getArguments().get(0).acceptTermVisitor(GlobalFactorWeightProcessor.this);
          functionApplication.getArguments().get(1).acceptTermVisitor(GlobalFactorWeightProcessor.this);
        }

        public void visitDoubleCast(DoubleCast doubleCast) {
          functionApplication.getArguments().get(0).acceptTermVisitor(GlobalFactorWeightProcessor.this);
        }

        public void visitDoubleAbs(DoubleAbs doubleAbs) {
          functionApplication.getArguments().get(0).acceptTermVisitor(GlobalFactorWeightProcessor.this);
        }

        public void visitDoubleAdd(DoubleAdd doubleAdd) {
          functionApplication.getArguments().get(0).acceptTermVisitor(GlobalFactorWeightProcessor.this);
          functionApplication.getArguments().get(1).acceptTermVisitor(GlobalFactorWeightProcessor.this);
        }

        public void visitDoubleMinus(DoubleMinus doubleMinus) {
          functionApplication.getArguments().get(0).acceptTermVisitor(GlobalFactorWeightProcessor.this);
          functionApplication.getArguments().get(1).acceptTermVisitor(GlobalFactorWeightProcessor.this);
        }

        public void visitWeightFunction(WeightFunction weightFunction) {
          context.prefixes.add("weights");
          RelationVariable weightsVar = weights.getRelation(weightFunction);
          context.relations.add(weightsVar);
          for (Map.Entry<String, Term> entry : context.remainingHiddenArgs.entrySet()) {
            exprBuilder.attribute("weights", weightsVar.type().heading().attribute(entry.getKey()));
            Term resolved = termResolver.resolve(entry.getValue(), context.var2term);
            if (!termResolver.allResolved())
              throw new RuntimeException("Arguments of the weight function must all be bound but " + entry.getValue() +
                      " is not");
            Expression expr = exprGenerator.convertTerm(resolved, groundAtoms, weights, context.var2expr, context.var2term);
            exprBuilder.expr(expr).equality();
            context.conditions.add(exprBuilder.getBool());
          }
//              ExpressionBuilder exprBuilder = TheBeast.getInstance().getNodServer().expressionBuilder();
//              exprBuilder.expr(weights.getRelation(weightFunction)).from("weights");
//              exprBuilder.expr(weights.getWeights()).intAttribute("weights","index").doubleArrayElement();
//              exprBuilder.num(0.0).inequality();
//              context.conditions.add(exprBuilder.getBool());
        }
      });
    }
  }
}
