package thebeast.pml.formula;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class DNFGenerator implements BooleanFormulaVisitor {

  private BooleanFormula formula;

  public void visitAtom(Atom atom) {
    formula = atom;
  }

  public void visitConjunction(Conjunction conjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    LinkedList<Disjunction> disjunctions = new LinkedList<Disjunction>();
    for (BooleanFormula arg : conjunction.getArguments()) {
      arg.acceptBooleanFormulaVisitor(this);
      if (formula instanceof Conjunction) {
        Conjunction sub = (Conjunction) formula;
        for (BooleanFormula subArg : sub.getArguments())
          args.add(subArg);
      } else if (formula instanceof Disjunction) {
        disjunctions.add((Disjunction) formula);
      } else
        args.add(formula);
    }
    if (disjunctions.size() == 0)
      formula = new Conjunction(args);
    else {
      formula = new Disjunction(createCrossProduct(args, disjunctions));
    }
  }

  public void visitDisjunction(Disjunction disjunction) {
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    for (BooleanFormula arg : disjunction.getArguments()) {
      arg.acceptBooleanFormulaVisitor(this);
      if (formula instanceof Disjunction) {
        Disjunction sub = (Disjunction) formula;
        for (BooleanFormula subArg : sub.getArguments())
          args.add(subArg);
      } else
        args.add(formula);
    }
    formula = new Disjunction(args);
  }

  private List<Conjunction> createCrossProduct(List<BooleanFormula> prefix, List<Disjunction> args) {
    LinkedList<Conjunction> result = new LinkedList<Conjunction>();
    for (Conjunction conjunction : createCrossProduct(args)) {
      LinkedList<BooleanFormula> enlarged = new LinkedList<BooleanFormula>();
      enlarged.addAll(prefix);
      enlarged.addAll(conjunction.getArguments());
      result.add(new Conjunction(enlarged));
    }
    return result;
  }

  private List<Conjunction> createCrossProduct(List<Disjunction> disjunctions) {
    if (disjunctions.size() == 0) {
      LinkedList<Conjunction> list = new LinkedList<Conjunction>();
      list.add(new Conjunction(new LinkedList<BooleanFormula>()));
      return list;
    }
    LinkedList<Conjunction> result = new LinkedList<Conjunction>();
    for (BooleanFormula formula : disjunctions.get(0).getArguments()) {
      for (Conjunction conjunction : createCrossProduct(disjunctions.subList(1, disjunctions.size()))) {
        LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
        if (formula instanceof Conjunction) {
          Conjunction c = (Conjunction) formula;
          args.addAll(c.getArguments());
        } else {
          args.add(formula);
        }
        args.addAll(conjunction.getArguments());
        result.add(new Conjunction(args));
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

  public DNF convertToDNF(BooleanFormula formula) {
    FormulaRegularizer.transform(formula).acceptBooleanFormulaVisitor(this);
    return new DNF(this.formula);
  }

  public static DNF generateDNF(BooleanFormula formula) {
    DNFGenerator generator = new DNFGenerator();
    FormulaRegularizer.transform(formula).acceptBooleanFormulaVisitor(generator);
    return new DNF(generator.formula);
  }

}
