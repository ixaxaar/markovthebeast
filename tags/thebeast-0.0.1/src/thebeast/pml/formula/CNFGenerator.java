package thebeast.pml.formula;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class CNFGenerator implements BooleanFormulaVisitor {

  private BooleanFormula formula;

  public void visitAtom(Atom atom) {
    formula = atom;
  }

  public void visitConjunction(Conjunction conjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    for (BooleanFormula arg : conjunction.getArguments()) {
      arg.acceptBooleanFormulaVisitor(this);
      if (formula instanceof Conjunction) {
        Conjunction sub = (Conjunction) formula;
        for (BooleanFormula subArg : sub.getArguments())
          args.add(subArg);
      } else
        args.add(formula);
    }
    formula = new Conjunction(args);
  }

  public void visitDisjunction(Disjunction disjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    LinkedList<Conjunction> conjunctions = new LinkedList<Conjunction>();
    for (BooleanFormula arg : disjunction.getArguments()) {
      arg.acceptBooleanFormulaVisitor(this);
      if (formula instanceof Disjunction) {
        Disjunction sub = (Disjunction) formula;
        for (BooleanFormula subArg : sub.getArguments())
          args.add(subArg);
      } else if (formula instanceof Conjunction) {
        conjunctions.add((Conjunction) formula);
      } else
        args.add(formula);
    }
    if (conjunctions.size() == 0)
      formula = new Disjunction(args);
    else {
      formula = new Conjunction(createCrossProduct(args, conjunctions));
    }
  }

  private List<Disjunction> createCrossProduct(List<BooleanFormula> prefix, List<Conjunction> args) {
    LinkedList<Disjunction> result = new LinkedList<Disjunction>();
    for (Disjunction disjunction : createCrossProduct(args)) {
      LinkedList<BooleanFormula> enlarged = new LinkedList<BooleanFormula>();
      enlarged.addAll(prefix);
      enlarged.addAll(disjunction.getArguments());
      result.add(new Disjunction(enlarged));
    }
    return result;
  }

  private List<Disjunction> createCrossProduct(List<Conjunction> conjunctions) {
    if (conjunctions.size() == 0) {
      LinkedList<Disjunction> list = new LinkedList<Disjunction>();
      list.add(new Disjunction(new LinkedList<BooleanFormula>()));
      return list;
    }
    LinkedList<Disjunction> result = new LinkedList<Disjunction>();
    for (BooleanFormula formula : conjunctions.get(0).getArguments()) {
      for (Disjunction disjunction : createCrossProduct(conjunctions.subList(1, conjunctions.size()))) {
        LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
        if (formula instanceof Disjunction) {
          Disjunction c = (Disjunction) formula;
          args.addAll(c.getArguments());
        } else {
          args.add(formula);
        }
        args.add(formula);
        args.addAll(disjunction.getArguments());
        result.add(new Disjunction(args));
      }
    }
    return result;
  }

  public void visitImplication(Implication implication) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    args.add(new Not(implication.getPremise()));
    args.add(implication.getConclusion());
    new Disjunction(args).acceptBooleanFormulaVisitor(this);
  }

  public void visitNot(Not not) {
    //not.getArgument().acceptBooleanFormulaVisitor(this);
    formula = not.getArgument();
    if (formula instanceof Atom) {
      formula = not;
    } else if (formula instanceof Disjunction) {
      Disjunction disjunction = (Disjunction) formula;
      LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
      for (BooleanFormula arg : disjunction.getArguments()) {
        args.add(new Not(arg));
      }
      new Conjunction(args).acceptBooleanFormulaVisitor(this);
    } else if (formula instanceof Conjunction) {
      Conjunction conjunction = (Conjunction) formula;
      LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
      for (BooleanFormula arg : conjunction.getArguments()) {
        args.add(new Not(arg));
      }
      new Disjunction(args).acceptBooleanFormulaVisitor(this);
    } else if (formula instanceof Not) {
      ((Not) formula).getArgument().acceptBooleanFormulaVisitor(this);
    }
  }

  public void visitAcyclicityConstraint(AcyclicityConstraint acyclicityConstraint) {

  }

  public BooleanFormula convert(BooleanFormula formula) {
    FormulaRegularizer.transform(formula).acceptBooleanFormulaVisitor(this);
    return this.formula;
  }

  public CNF convertToCNF(BooleanFormula formula) {
    FormulaRegularizer.transform(formula).acceptBooleanFormulaVisitor(this);
    return new CNF(this.formula);
  }

  public static CNF generateCNF(BooleanFormula formula){
    CNFGenerator generator = new CNFGenerator();
    formula.acceptBooleanFormulaVisitor(generator);
    return new CNF(generator.formula);
  }



}
