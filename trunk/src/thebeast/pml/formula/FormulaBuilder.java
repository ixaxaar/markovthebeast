package thebeast.pml.formula;

import thebeast.pml.*;
import thebeast.pml.function.Function;
import thebeast.pml.function.FunctionTypeException;
import thebeast.pml.function.IntAdd;
import thebeast.pml.function.IntMinus;
import thebeast.pml.predicate.PredicateTypeException;
import thebeast.pml.predicate.IntLEQ;
import thebeast.pml.predicate.Predicate;
import thebeast.pml.term.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:51:44
 */
public class FormulaBuilder {

  private Stack<Object> termStack = new Stack<Object>();
  private Stack<BooleanFormula> formulaStack = new Stack<BooleanFormula>();
  private Stack<Quantification> quantificationStack = new Stack<Quantification>();

  private BooleanFormula condition;
  private Term weight, upperBound, lowerBound;
  private BooleanFormula formula, count;
  private Signature signature;
  private HashMap<String, Variable> name2var = new HashMap<String, Variable>();

  private LinkedList<Variable> vars = new LinkedList<Variable>();

  private int formulaCount = 0;

  public FormulaBuilder(Signature signature) {
    this.signature = signature;
  }

  public FormulaBuilder atom(String predicateName) {
    Predicate predicate = signature.getPredicate(predicateName);
    return atom(predicate);
  }

  public FormulaBuilder atom(Predicate predicate) {
    int index = predicate.getArgumentTypes().size() - 1;
    LinkedList<Term> args = new LinkedList<Term>();
    while (index >= 0) {
      Type type = predicate.getArgumentTypes().get(index);
      Object obj = termStack.pop();
      Term term;
      term = toTerm(type, obj);
      if (term != DontCare.DONTCARE && !term.getType().equals(type))
        throw new PredicateTypeException(predicate, index, term);
      args.addFirst(term);
      --index;
    }
    formulaStack.push(new PredicateAtom(predicate, args));
    return this;
  }

  public FormulaBuilder apply(String functionName) {
    Function function = signature.getFunction(functionName);
    return apply(function);
  }

  public FormulaBuilder apply(Function function) {
    int index = function.getArgumentTypes().size() - 1;
    LinkedList<Term> args = new LinkedList<Term>();
    while (index >= 0) {
      Type type = function.getArgumentTypes().get(index);
      Object obj = termStack.pop();
      Term term;
      term = toTerm(type, obj);
      if (term != DontCare.DONTCARE && !term.getType().equals(type))
        throw new FunctionTypeException(function, index, term);
      args.addFirst(term);
      --index;
    }
    termStack.push(new FunctionApplication(function, args));
    return this;
  }

  private Term toTerm(Type type, Object obj) {
    Term term;
    if (obj instanceof Term) {
      term = (Term) obj;
    } else if (obj instanceof Integer) {
      term = new IntConstant(type, (Integer) obj);
    } else if (obj instanceof String) {
      if (type.getTypeClass() != Type.Class.CATEGORICAL && type.getTypeClass() != Type.Class.CATEGORICAL_UNKNOWN)
        throw new RuntimeException("Strings must be of categorical type");
      CategoricalConstant constant = (CategoricalConstant) type.getConstant((String) obj);
      if (constant == null)
        throw new RuntimeException(obj.toString() + " is not a member of type " + type.getName());
      term = new CategoricalConstant(type, constant.getName());
    } else {
      throw new RuntimeException(obj + " can't be converted to a term");
    }
    return term;
  }

  private Term toTerm(Object obj) {
    Term term;
    if (obj instanceof Term) {
      term = (Term) obj;
    } else if (obj instanceof Integer) {
      term = new IntConstant(Type.INT, (Integer) obj);
    } else if (obj instanceof Double) {
      term = new DoubleConstant(Type.DOUBLE, (Double) obj);
    } else {
      throw new RuntimeException(obj + " can't be converted to a term");
    }
    return term;
  }

  public FormulaBuilder term(Object term) {
    termStack.push(term);
    return this;
  }

  public FormulaBuilder condition() {
    if (formulaStack.size() > 1)
      throw new RuntimeException("There is more than one formula on the stack -- " +
              "do you really want " + formulaStack.peek() + " to be the condition");
    condition = formulaStack.pop();
    return this;
  }

  public FormulaBuilder weight() {
    weight = toTerm(termStack.pop());
    return this;
  }

  public FormulaBuilder formula() {
    if (formulaStack.size() > 1)
      throw new RuntimeException("There is more than one formula on the stack -- " +
              "do you really want " + formulaStack.peek() + " to be the formula");
    formula = formulaStack.pop();
    return this;
  }


  public FormulaBuilder var(Type type, String name) {
    Variable var = new Variable(type, name);
    vars.add(var);
    name2var.put(name, var);
    return this;
  }

  public FormulaBuilder var(String type, String name) {
    Variable var = new Variable(signature.getType(type), name);
    vars.add(var);
    name2var.put(name, var);
    return this;
  }


  public FormulaBuilder var(String name) {
    Variable var = name2var.get(name);
    termStack.push(var);
    return this;
  }

  public FormulaBuilder var(Variable var) {
    termStack.push(var);
    return this;
  }


  public FormulaBuilder quantify() {
    quantificationStack.push(new Quantification(vars));
    vars.clear();
    return this;
  }

  private Quantification popQuantification() {
    if (quantificationStack.isEmpty()) return new Quantification(new ArrayList<Variable>());
    Quantification quantification = quantificationStack.pop();
    for (Variable var : quantification.getVariables()) {
      name2var.remove(var.getName());
    }
    return quantification;
  }

  public FactorFormula produceFactorFormula(String name) {
    FactorFormula factorFormula = new FactorFormula(name, popQuantification(), condition, formula,
            weight == null ? new DoubleConstant(Double.POSITIVE_INFINITY) : weight );
    condition = null;
    formula = null;
    weight = null;
    return factorFormula;
  }

  public FactorFormula produceFactorFormula() {
    return produceFactorFormula("@formula" + formulaCount++);
  }

  public BooleanFormula getFormula() {
    return formulaStack.pop();
  }

  public FormulaBuilder dontCare() {
    termStack.push(DontCare.DONTCARE);
    return this;
  }

  public FormulaBuilder and(int howmany) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    for (int i = 0; i < howmany; ++i)
      args.addFirst(formulaStack.pop());
    formulaStack.push(new Conjunction(args));
    return this;
  }

  public FormulaBuilder or(int howmany) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    for (int i = 0; i < howmany; ++i)
      args.addFirst(formulaStack.pop());
    formulaStack.push(new Disjunction(args));
    return this;
  }

  public FormulaBuilder implies() {
    BooleanFormula conclusion = formulaStack.pop();
    BooleanFormula premise = formulaStack.pop();
    formulaStack.push(new Implication(premise, conclusion));
    return this;
  }

  public FormulaBuilder equality() {
    Term rhs = toTerm(termStack.pop());
    Term lhs = toTerm(termStack.pop());
    if (!rhs.getType().equals(lhs.getType()))
      throw new TypeMismatchException("For an equals(arg1,arg2) atom types must match", rhs.getType(), lhs.getType());
    formulaStack.push(new PredicateAtom(signature.createEquals(lhs.getType()), lhs, rhs));
    return this;
  }

  public FormulaBuilder not() {
    formulaStack.push(new Not(formulaStack.pop()));
    return this;
  }

  public FormulaBuilder add() {
    apply(IntAdd.ADD);
    return this;
  }

  public FormulaBuilder minus() {
      apply(IntMinus.MINUS);
      return this;
    }

  public FormulaBuilder intLEQ() {
    Term rhs = toTerm(termStack.pop());
    Term lhs = toTerm(termStack.pop());
    if (!lhs.getType().equals(Type.INT))
      throw new TypeMismatchException("intLEQ needs an integer lhs", rhs.getType(), lhs.getType());
    if (!rhs.getType().equals(Type.INT))
      throw new TypeMismatchException("intLEQ needs an integer rhs", rhs.getType(), lhs.getType());
    formulaStack.push(new PredicateAtom(IntLEQ.INT_LEQ, lhs, rhs));
    return this;
  }

  public FormulaBuilder clear() {
    formulaStack.clear();
    termStack.clear();
    quantificationStack.clear();
    return this;
  }

  public FormulaBuilder inequality() {
    Term rhs = toTerm(termStack.pop());
    Term lhs = toTerm(termStack.pop());
    if (!rhs.getType().equals(lhs.getType()))
      throw new TypeMismatchException("For an equals(arg1,arg2) atom types must match", rhs.getType(), lhs.getType());
    formulaStack.push(new PredicateAtom(signature.createNotEquals(lhs.getType()), lhs, rhs));
    return this;
  }

  public FormulaBuilder cardinality() {
    count = formulaStack.pop();
    return this;
  }

  public FormulaBuilder upperBound() {
    upperBound = toTerm(termStack.pop());
    return this;
  }

  public FormulaBuilder lowerBound() {
    lowerBound = toTerm(termStack.pop());
    return this;
  }

  public FormulaBuilder cardinalityConstraint(boolean useClosure) {
    formulaStack.push(new CardinalityConstraint(
            lowerBound == null ? new IntConstant(Integer.MIN_VALUE) : lowerBound,
            popQuantification(),
            count,
            upperBound == null ? new IntConstant(Integer.MAX_VALUE) : upperBound,useClosure));
    return this;
  }

  public FormulaBuilder aclicity(UserPredicate predicate) {
    formulaStack.push(new AcyclicityConstraint(predicate));
    return this;
  }
}
