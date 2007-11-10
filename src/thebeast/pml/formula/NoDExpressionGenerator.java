package thebeast.pml.formula;

import thebeast.nod.expression.BoolExpression;
import thebeast.nod.expression.Expression;
import thebeast.nod.expression.ExpressionFactory;
import thebeast.nod.expression.Operator;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.type.DoubleType;
import thebeast.nod.type.IntType;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.pml.*;
import thebeast.pml.function.*;
import thebeast.pml.predicate.*;
import thebeast.pml.term.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * The NoD expression generator creates database expression for boolean formulas and terms.
 */
public class NoDExpressionGenerator implements BooleanFormulaVisitor, TermVisitor {

  private Map<Variable, Expression> var2expr;
  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
  private GroundAtoms groundAtoms;
  private Weights weights;
  private Map<Variable, Term> var2term;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();

  public Expression convertFormula(BooleanFormula formula, GroundAtoms groundAtoms, Weights weights,
                                   Map<Variable, Expression> var2expr, Map<Variable, Term> var2term) {
    this.var2expr = var2expr;
    this.groundAtoms = groundAtoms;
    this.weights = weights;
    this.var2term = var2term;
    formula.acceptBooleanFormulaVisitor(this);
    return builder.getExpression();
  }

  public Expression convertTerm(Term term, GroundAtoms groundAtoms, Weights weights,
                                Map<Variable, Expression> var2expr, Map<Variable, Term> var2term) {
    this.var2expr = var2expr;
    this.groundAtoms = groundAtoms;
    this.weights = weights;
    this.var2term = var2term;
    term.acceptTermVisitor(this);
    return builder.getExpression();
  }


  public void visitAtom(final Atom atom) {
    atom.acceptAtomVisitor(new AbstractAtomVisitor() {
      public void visitUndefinedWeight(UndefinedWeight undefinedWeight) {
        WeightFunction f = (WeightFunction) undefinedWeight.getFunctionApplication().getFunction();
        builder.expr(weights.getRelation(f));
        int index = 0;
        List<thebeast.nod.variable.Variable> operatorArgs = new ArrayList<thebeast.nod.variable.Variable>();
        int conditionCount = 0;
        for (Term arg : undefinedWeight.getFunctionApplication().getArguments()) {
          if (arg != DontCare.DONTCARE) {
            thebeast.nod.variable.Variable
                    var = interpreter.createVariable(f.getHeading().attribute(f.getColumnName(index)).type());
            operatorArgs.add(var);
            builder.attribute(f.getHeading().attribute(f.getColumnName(index)));
            builder.expr(var);
            builder.equality();
            ++conditionCount;
          }
          ++index;
        }
        builder.and(conditionCount).restrict().count();
        Operator<IntType> numberOfFeatures =
                factory.createOperator("numFeatures", operatorArgs, builder.getInt());

        for (Term arg : undefinedWeight.getFunctionApplication().getArguments()) {
          if (arg != DontCare.DONTCARE) {
            arg.acceptTermVisitor(NoDExpressionGenerator.this);
          }
        }
        builder.invokeIntOp(numberOfFeatures).num(0).equality();

      }

      public void visitPredicateAtom(final PredicateAtom atom) {



        atom.getPredicate().acceptPredicateVisitor(new PredicateVisitor() {
          public void visitUserPredicate(UserPredicate userPredicate) {
            //TODO: this doesn't work for atoms with DONTCARES (because the contains expression needs all args)
            builder.expr(groundAtoms.getGroundAtomsOf(userPredicate).getRelationVariable());
            int index = 0;
            for (Term arg : atom.getArguments()) {
              if (arg != DontCare.DONTCARE) {
                builder.id(userPredicate.getColumnName(index++));
                arg.acceptTermVisitor(NoDExpressionGenerator.this);
              }
            }
            builder.tupleForIds();
            builder.contains();

          }

          public void visitEquals(Equals equals) {
            atom.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
            atom.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
            builder.equality();
          }

          public void visitIntLEQ(IntLEQ intLEQ) {
            atom.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
            atom.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
            builder.intLEQ();
          }

          public void visitNotEquals(NotEquals notEquals) {
            atom.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
            atom.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
            builder.inequality();
          }

          public void visitIntLT(IntLT intLT) {
            atom.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
            atom.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
            builder.intLessThan();
          }

          public void visitIntGT(IntGT intGT) {
            atom.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
            atom.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
            builder.intGreaterThan();
          }

          public void visitIntGEQ(IntGEQ intGEQ) {
            atom.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
            atom.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
            builder.intGEQ();
          }


        });
      }

      public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
        Term lb = cardinalityConstraint.getLowerBound();
        Term ub = cardinalityConstraint.getUpperBound();
        TermResolver resolver = new TermResolver();
        boolean hasLb = false;
        if (!cardinalityConstraint.isLEQ()) {
          resolver.resolve(lb,var2term).acceptTermVisitor(NoDExpressionGenerator.this);
          //lb.acceptTermVisitor(NoDExpressionGenerator.this);
          hasLb = true;
        }
        createCountQuery(cardinalityConstraint.getQuantification(), cardinalityConstraint.getFormula());
        Expression count = builder.getExpression();
        if (hasLb) {
          if (cardinalityConstraint.getSign())
            builder.expr(count).intLEQ();
          else
            builder.expr(count).intGreaterThan();
          //builder.expr(count);
        }
        if (!cardinalityConstraint.isGEQ()) {
          builder.expr(count);
          resolver.resolve(ub,var2term).acceptTermVisitor(NoDExpressionGenerator.this);
          //ub.acceptTermVisitor(NoDExpressionGenerator.this);
          if (cardinalityConstraint.getSign())
            builder.intLEQ();
          else
            builder.intGreaterThan();
          if (hasLb)
            if (cardinalityConstraint.getSign())
              builder.and(2);
            else
              builder.or(2);
        }

      }

      public void visitTrue(True aTrue) {
        builder.bool(true);
      }

    });
  }

  public void createCountQuery(Quantification quantification, BooleanFormula formula) {
    DNF dnf = DNFGenerator.generateDNF(formula);
    if (dnf.getConjunctionCount() > 1)
      throw new RuntimeException("We can only do plain conjunctions inside cardinality constraints but look at this:" +
              dnf + " coming from this " + formula);
    List<SignedAtom> conjunction = dnf.getConjunction(0);

    ConjunctionProcessor processor = new ConjunctionProcessor(weights, groundAtoms);


    UnresolvedVariableCollector collector = new UnresolvedVariableCollector();
    collector.bind(quantification.getVariables());

    formula.acceptBooleanFormulaVisitor(collector);

    LinkedList<Expression> args = new LinkedList<Expression>();
    LinkedList<thebeast.nod.variable.Variable> params = new LinkedList<thebeast.nod.variable.Variable>();
    ConjunctionProcessor.Context context = new ConjunctionProcessor.Context();
    context.var2expr.putAll(var2expr);
    for (Variable var : collector.getUnresolved()) {
      thebeast.nod.variable.Variable param = interpreter.createVariable(var.getType().getNodType());
      Term term = var2term.get(var);
      if (term == null) throw new UnresolvableVariableException(var);
      NoDExpressionGenerator generator = new NoDExpressionGenerator();
      Expression arg = generator.convertTerm(term, groundAtoms, weights, var2expr, var2term);
      args.add(arg);
      params.add(param);
      //make sure the processor can replace this variable with an actual expression (the param)
      context.var2expr.put(var, param);
      //make sure the conjunction processor is able to resolve the externally bound variables
      context.var2term.put(var, var);
    }

    processor.processConjunction(context, conjunction);

    //we select all quantified variables
    int index = 0;
    for (Variable var : quantification.getVariables()) {
      NoDExpressionGenerator generator = new NoDExpressionGenerator();
      TermResolver resolver = new TermResolver();
      Term resolved = resolver.resolve(var, context.var2term);
      Expression expr = generator.convertTerm(resolved, groundAtoms, weights, context.var2expr, context.var2term);
      context.selectBuilder.id("var" + index++).expr(expr);
    }
    context.selectBuilder.tuple(quantification.getVariables().size());
    //todo: we need to create an operator that takes as input the exprs in var2term
    ExpressionBuilder opBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

    //for (BoolExpression condition : context.conditions) builder.expr(condition);
    BoolExpression where = factory.createAnd(context.conditions);
    builder.expr(factory.createQuery(context.prefixes, context.relations, where, context.selectBuilder.getTuple()));
    builder.count();
    Operator<IntType> op = factory.createOperator("count", params, builder.getInt());
    builder.expressions(args).invokeIntOp(op);
  }

  public void visitConjunction(Conjunction conjunction) {
    for (BooleanFormula arg : conjunction.getArguments())
      arg.acceptBooleanFormulaVisitor(this);
    builder.and(conjunction.getArguments().size());
  }

  public void visitDisjunction(Disjunction disjunction) {
    for (BooleanFormula arg : disjunction.getArguments())
      arg.acceptBooleanFormulaVisitor(this);
    builder.and(disjunction.getArguments().size());
  }

  public void visitImplication(Implication implication) {
    implication.getPremise().acceptBooleanFormulaVisitor(this);
    builder.not();
    implication.getConclusion().acceptBooleanFormulaVisitor(this);
    builder.or(2);
  }

  public void visitNot(Not not) {
    not.getArgument().acceptBooleanFormulaVisitor(this);
    builder.not();
  }

  public void visitAcyclicityConstraint(AcyclicityConstraint acyclicityConstraint) {
    builder.cycles(acyclicityConstraint.getPredicate().getAttribute(0).name(),
            acyclicityConstraint.getPredicate().getAttribute(1).name());
    builder.count().num(0).equality();
  }


  public void visitVariable(Variable variable) {
    Expression expression = var2expr.get(variable);
    if (expression == null)
      throw new UnresolvableVariableException(variable);
    builder.expr(expression);
  }

  public static class UnresolvableVariableException extends RuntimeException {
    private Variable variable;

    public UnresolvableVariableException(Variable variable) {
      super("Variable " + variable + " has nothing to replace it with");
      this.variable = variable;
    }

    public Variable getVariable() {
      return variable;
    }
  }

  public void visitFunctionApplication(final FunctionApplication functionApplication) {
    functionApplication.getFunction().acceptFunctionVisitor(new FunctionVisitor() {
      public void visitWeightFunction(WeightFunction weightFunction) {
        Expression[] args = new Expression[functionApplication.getArguments().size()];
        for (int i = 0; i < args.length; ++i) {
          functionApplication.getArguments().get(i).acceptTermVisitor(NoDExpressionGenerator.this);
          args[i] = builder.getExpression();
        }
        builder.expr(weights.getWeightExpression(weightFunction, args));
      }

      public void visitIntAdd(IntAdd intAdd) {
        functionApplication.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
        functionApplication.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
        builder.intAdd();
      }

      public void visitIntMinus(IntMinus intMinus) {
        functionApplication.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
        functionApplication.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
        builder.intMinus();

      }

      public void visitIntMin(IntMin intMin) {
        functionApplication.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
        functionApplication.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
        builder.intMin();

      }

      public void visitIntMax(IntMax intMax) {
        functionApplication.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
        functionApplication.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
        builder.intMax();
      }

      public void visitDoubleProduct(DoubleProduct doubleProduct) {
        functionApplication.getArguments().get(0).acceptTermVisitor(NoDExpressionGenerator.this);
        functionApplication.getArguments().get(1).acceptTermVisitor(NoDExpressionGenerator.this);
        builder.doubleTimes();
      }
    });
  }

  public void visitIntConstant(IntConstant intConstant) {
    builder.integer((IntType) intConstant.getType().getNodType(), intConstant.getInteger());
  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    CategoricalType type = (CategoricalType) categoricalConstant.getType().getNodType();
    if (type.index(categoricalConstant.getName()) == -1)
      throw new RuntimeException(categoricalConstant.getName() + " is not a member of the expected type");
    builder.categorical(type, categoricalConstant.getName());
  }

  public void visitDontCare(DontCare dontCare) {
    throw new RuntimeException("Can't do 'don't cares'");
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    builder.doubleValue((DoubleType) doubleConstant.getType().getNodType(), doubleConstant.getValue());
  }

  public void visitBinnedInt(BinnedInt binnedInt) {
    binnedInt.getArgument().acceptTermVisitor(this);
    builder.bins(binnedInt.getBins());
  }

  public void visitBoolConstant(BoolConstant boolConstant) {
    builder.bool(boolConstant.getBool());
  }
}
