package thebeast.pml.formula;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:35:13
 */
public interface BooleanFormulaVisitor {
  void visitAtom(Atom atom);

  void visitConjunction(Conjunction conjunction);

  void visitDisjunction(Disjunction disjunction);

  void visitImplication(Implication implication);

  void visitNot(Not not);

  void visitAcyclicityConstraint(AcyclicityConstraint acyclicityConstraint);
}
