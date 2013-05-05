package thebeast.pml.parser;

import thebeast.pml.Quantification;
import thebeast.pml.Signature;
import thebeast.pml.Type;
import thebeast.pml.UserPredicate;
import thebeast.pml.formula.*;
import thebeast.pml.function.*;
import thebeast.pml.predicate.*;
import thebeast.pml.term.*;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Nov-2007 Time: 23:52:05
 */
public class FormulaParser implements ParserFormulaVisitor, ParserTermVisitor {

  private Signature signature;
  private BooleanFormula formula;
  private Term term;
  private Stack<Type> typeContext = new Stack<Type>();
  private Stack<HashMap<String, Variable>> variables = new Stack<HashMap<String, Variable>>();
  private ParserFactorFormula rootFactor;

  public FormulaParser(Signature signature) {
    this.signature = signature;
  }

  public FactorFormula build(String text){
    PMLParser parser = new PMLParser(new Yylex(new ByteArrayInputStream((text+";").getBytes())));
    try {
        ParserFactorFormula formula = (ParserFactorFormula) ((List) parser.parse().value).get(0);
        return build(formula);
    } catch (PMLParseException e) {
      try {
        throw new RuntimeException(errorMessage(e, new ByteArrayInputStream(text.getBytes())));
      } catch (IOException e1) {
        throw new RuntimeException("Somethings really wrong with " + text, e1);
      }
    } catch (Exception e) {
      throw new RuntimeException("Somethings really wrong with " + text, e);
    }
  }

  public FactorFormula build(ParserFactorFormula parserFactorFormula) {
    rootFactor = parserFactorFormula;
    Quantification quantification = parserFactorFormula.quantification == null ?
            new Quantification(new ArrayList<Variable>()) :
            pushQuantification(parserFactorFormula.quantification);
    if (parserFactorFormula.condition != null)
      parserFactorFormula.condition.acceptParserFormulaVisitor(this);
    else
      formula = null;
    BooleanFormula condition = formula;
    parserFactorFormula.formula.acceptParserFormulaVisitor(this);
    BooleanFormula formula = this.formula;
    typeContext.push(Type.DOUBLE);
    parserFactorFormula.weight.acceptParserTermVisitor(this);
    typeContext.pop();
    Term weight = term;
    FactorFormula factorFormula = new FactorFormula(parserFactorFormula.spec.name, quantification, condition, formula, weight);
    factorFormula.setOrder(parserFactorFormula.spec.order);
    factorFormula.setGround(parserFactorFormula.spec.ground);
    if (parserFactorFormula.quantification != null)
      popQuantification();
    return factorFormula;
  }

  public Quantification pushQuantification(List<ParserTyping> vars) {
    LinkedList<Variable> quantification = new LinkedList<Variable>();
    HashMap<String, Variable> map = new HashMap<String, Variable>();
    for (ParserTyping typing : vars) {
      Variable variable = new Variable(signature.getType(typing.type), typing.var);
      quantification.add(variable);
      map.put(typing.var, variable);
    }
    variables.push(map);
    return new Quantification(quantification);
  }

  public Map<String, Variable> popQuantification() {
    return variables.pop();
  }


  public void visitAtom(ParserAtom parserAtom) {
    LinkedList<Term> args = new LinkedList<Term>();
    Predicate predicate = signature.getPredicate(parserAtom.predicate);
    if (predicate == null)
      throw new ShellException("There is no predicate called " + parserAtom.predicate);
    int index = 0;
    if (parserAtom.args.size() != predicate.getArity())
      throw new ShellException("Predicate " + predicate.getName() + " has " + predicate.getArity()
              + " arguments, not " + parserAtom.args.size() + " as in " + parserAtom);
    for (ParserTerm term : parserAtom.args) {
      typeContext.push(predicate.getArgumentTypes().get(index++));
      term.acceptParserTermVisitor(this);
      args.add(this.term);
      typeContext.pop();
    }
    formula = new PredicateAtom(predicate, args);
  }

  public void visitConjuction(ParserConjunction parserConjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    parserConjunction.lhs.acceptParserFormulaVisitor(this);
    args.add(this.formula);
    ParserFormula rhs = parserConjunction.rhs;
    while (rhs instanceof ParserConjunction) {
      ParserConjunction c = (ParserConjunction) rhs;
      c.lhs.acceptParserFormulaVisitor(this);
      args.add(this.formula);
      rhs = c.rhs;
    }
    rhs.acceptParserFormulaVisitor(this);
    args.add(this.formula);
    formula = new Conjunction(args);
  }

  public void visitDisjunction(ParserDisjunction parserDisjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    parserDisjunction.lhs.acceptParserFormulaVisitor(this);
    args.add(this.formula);
    ParserFormula rhs = parserDisjunction.rhs;
    while (rhs instanceof ParserDisjunction) {
      ParserDisjunction c = (ParserDisjunction) rhs;
      c.lhs.acceptParserFormulaVisitor(this);
      args.add(this.formula);
      rhs = c.rhs;
    }
    rhs.acceptParserFormulaVisitor(this);
    args.add(this.formula);
    formula = new Disjunction(args);
  }
    
  public void visitImplies(ParserImplies parserImplies) {
    parserImplies.lhs.acceptParserFormulaVisitor(this);
    BooleanFormula lhs = this.formula;
    parserImplies.rhs.acceptParserFormulaVisitor(this);
    BooleanFormula rhs = this.formula;
    formula = new Implication(lhs, rhs);
  }

  public void visitCardinalityConstraint(ParserCardinalityConstraint parserCardinalityConstraint) {
    typeContext.push(Type.INT);
    parserCardinalityConstraint.lowerBound.acceptParserTermVisitor(this);
    typeContext.pop();
    Term lb = term;
    Quantification quantification = pushQuantification(parserCardinalityConstraint.quantification);
    parserCardinalityConstraint.formula.acceptParserFormulaVisitor(this);
    popQuantification();
    typeContext.push(Type.INT);
    parserCardinalityConstraint.upperBound.acceptParserTermVisitor(this);
    typeContext.pop();
    Term ub = term;
    //formula = new CardinalityConstraint(lb, quantification, formula, ub,true);
    formula = new CardinalityConstraint(lb, quantification, formula, ub, parserCardinalityConstraint.useClosure);
  }

  public void visitComparison(ParserComparison parserComparison) {
    Term lhs, rhs;
    if (parserComparison.type == ParserComparison.Type.NEQ || parserComparison.type == ParserComparison.Type.EQ) {
      if (parserComparison.lhs instanceof ParserNamedConstant) {
        parserComparison.rhs.acceptParserTermVisitor(this);
        rhs = term;
        typeContext.push(rhs.getType());
        parserComparison.lhs.acceptParserTermVisitor(this);
        typeContext.pop();
        lhs = term;
      } else {
        parserComparison.lhs.acceptParserTermVisitor(this);
        lhs = term;
        typeContext.push(lhs.getType());
        parserComparison.rhs.acceptParserTermVisitor(this);
        typeContext.pop();
        rhs = term;

      }
    } else {
      parserComparison.lhs.acceptParserTermVisitor(this);
      lhs = term;
      parserComparison.rhs.acceptParserTermVisitor(this);
      rhs = term;
    }
    switch (parserComparison.type) {
      case EQ:
        formula = new PredicateAtom(signature.createEquals(Type.INT), lhs, rhs);
        break;
      case NEQ:
        formula = new PredicateAtom(signature.createNotEquals(Type.INT), lhs, rhs);
        break;
      case LEQ:
        formula = new PredicateAtom(IntLEQ.INT_LEQ, lhs, rhs);
        break;
      case LT:
        formula = new PredicateAtom(IntLT.INT_LT, lhs, rhs);
        break;
      case GT:
        formula = new PredicateAtom(IntGT.INT_GT, lhs, rhs);
        break;
      case GEQ:
        formula = new PredicateAtom(IntGEQ.INT_GEQ, lhs, rhs);
        break;
    }
  }

  public void visitAcyclicityConstraint(ParserAcyclicityConstraint parserAcyclicityConstraint) {
    UserPredicate predicate = (UserPredicate) signature.getPredicate(parserAcyclicityConstraint.predicate);
    formula = new AcyclicityConstraint(predicate);
  }

  public void visitNot(ParserNot parserNot) {
    parserNot.formula.acceptParserFormulaVisitor(this);
    formula = new Not(formula);
  }

  public void visitUndefinedWeight(ParserUndefinedWeight parserUndefinedWeight) {
    parserUndefinedWeight.functionApplication.acceptParserTermVisitor(this);
    formula = new UndefinedWeight((FunctionApplication) term);
  }

  public void visitNamedConstant(ParserNamedConstant parserNamedConstant) {
    term = typeContext.peek().getConstant(parserNamedConstant.name);
//typeCheck();
  }

  public void visitIntConstant(ParserIntConstant parserIntConstant) {
    term = new IntConstant(parserIntConstant.number);
    typeCheck();

  }

  public void visitParserAdd(ParserAdd parserAdd) {
    parserAdd.lhs.acceptParserTermVisitor(this);
    Term lhs = term;
    parserAdd.rhs.acceptParserTermVisitor(this);
    Term rhs = term;
    term = new FunctionApplication(IntAdd.ADD, lhs, rhs);
    typeCheck();
  }


  public void visitParserTimes(ParserTimes parserTimes) {
    parserTimes.lhs.acceptParserTermVisitor(this);
    Term lhs = term;
    parserTimes.rhs.acceptParserTermVisitor(this);
    Term rhs = term;
    term = new FunctionApplication(DoubleProduct.PRODUCT, lhs, rhs);
    typeCheck();
  }

  public void visitParserMinus(ParserMinus parserMinus) {
    parserMinus.lhs.acceptParserTermVisitor(this);
    Term lhs = term;
    parserMinus.rhs.acceptParserTermVisitor(this);
    Term rhs = term;
    term = new FunctionApplication(IntMinus.MINUS, lhs, rhs);
    typeCheck();
  }

  public void visitDontCare(ParserDontCare parserDontCare) {
    term = DontCare.DONTCARE;
//typeCheck();
  }

  public void visitFunctionApplication(ParserFunctionApplication parserFunctionApplication) {
    LinkedList<Term> args = new LinkedList<Term>();
    Function function = signature.getFunction(parserFunctionApplication.function);
    if (function == null)
      throw new ShellException("There is no function with name " + parserFunctionApplication.function);
    int index = 0;
    if (function.getArity() != parserFunctionApplication.args.size()) {
      throw new ShellException("Function " + function.getName() + " has arity " + function.getArity() + " but " +
              "it is applied with " + parserFunctionApplication.args.size() + " in " + parserFunctionApplication);
    }
    for (ParserTerm term : parserFunctionApplication.args) {
      typeContext.push(function.getArgumentTypes().get(index++));
      term.acceptParserTermVisitor(this);
      args.add(this.term);
      typeContext.pop();
    }
    term = new FunctionApplication(function, args);
    typeCheck();
  }

  public void visitDoubleConstant(ParserDoubleConstant parserDoubleConstant) {
    term = new DoubleConstant(parserDoubleConstant.number);
    typeCheck();
  }

  private Variable resolve(String name) {
    for (HashMap<String, Variable> scope : variables) {
      Variable var = scope.get(name);
      if (var != null) return var;
    }
    return null;
  }

  public void visitVariable(ParserVariable parserVariable) {
    term = resolve(parserVariable.name);
    WeightFunction function = signature.getWeightFunction(parserVariable.name);
    if (term != null && function != null)
      throw new ShellException("We don't like this ambiguity: " + parserVariable.name + " is both a variable " +
              "and a (zero-arity) weight function. Why we could resolve this in a clever manner, we refrain from doing so " +
              "to make sure you know what you're doing.");
    if (term == null) {
      if (function == null)
        throw new RuntimeException(parserVariable.name + " was not quantified in " + rootFactor);
      LinkedList<Term> args = new LinkedList<Term>();
      term = new FunctionApplication(function, args);
      typeCheck();
    } else
      typeCheck();

  }

  public void visitBins(ParserBins parserBins) {
    LinkedList<Integer> bins = new LinkedList<Integer>();
    for (ParserTerm term : parserBins.bins) {
      if (term instanceof ParserIntConstant) {
        ParserIntConstant intConstant = (ParserIntConstant) term;
        bins.add(intConstant.number);
      } else
        throw new ShellException("bins must be integers");
    }
    parserBins.argument.acceptParserTermVisitor(this);
    term = new BinnedInt(bins, term);
    typeCheck();
  }

  public void visitBoolConstant(ParserBoolConstant parserBoolConstant) {
    term = new BoolConstant(parserBoolConstant.value);
    typeCheck();
  }


  private void typeCheck() {
    if (!typeContext.isEmpty() && !term.getType().inherits(typeContext.peek()))
      throw new RuntimeException("Variable " + term + " must be of type " + typeContext.peek() + " in " +
              rootFactor);
  }

  public static String errorMessage(PMLParseException exception, InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    int lineNr = 0;
    int colNr = exception.getCol();
    for (String line = reader.readLine(); line != null; line = reader.readLine(), ++lineNr) {
      if (lineNr == exception.getLine()) {
        StringBuffer buffer = new StringBuffer(line);
        //System.out.println(buffer);
        buffer.insert(colNr, "!ERROR!");
        return "Error on line " + lineNr + ": " + buffer.toString();
      }
      colNr -= line.length() + 1;
    }
    return "Syntax Error!";
  }
  

}
