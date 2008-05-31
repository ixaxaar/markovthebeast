package thebeast.pml.formula;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Feb-2007 Time: 18:04:43
 */
public class FormulaRegularizer extends FormulaCopyVisitor {

  public static BooleanFormula transform(BooleanFormula formula){
    FormulaRegularizer regularizer = new FormulaRegularizer();
    formula.acceptBooleanFormulaVisitor(regularizer);
    return regularizer.formula;
  }

  public void visitImplication(Implication implication) {
    implication.getPremise().acceptBooleanFormulaVisitor(this);
    BooleanFormula premise = formula;
    implication.getConclusion().acceptBooleanFormulaVisitor(this);
    BooleanFormula conclusion = formula;
    LinkedList<BooleanFormula> args = new LinkedList<BooleanFormula>();
    args.add(new Not(premise));
    args.add(conclusion);
    formula = new Disjunction(args);
  }
}
