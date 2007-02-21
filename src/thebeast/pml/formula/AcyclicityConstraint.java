package thebeast.pml.formula;

import thebeast.pml.UserPredicate;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 13-Feb-2007 Time: 21:01:00
 */
public class AcyclicityConstraint extends BooleanFormula{

  private UserPredicate predicate;


  public AcyclicityConstraint(UserPredicate predicate) {
    this.predicate = predicate;
  }


  public UserPredicate getPredicate() {
    return predicate;
  }

  public void acceptBooleanFormulaVisitor(BooleanFormulaVisitor visitor) {
    visitor.visitAcyclicityConstraint(this);
  }
}
