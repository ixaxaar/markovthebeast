package thebeast.pml.formula;

import thebeast.pml.UserPredicate;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 13-Feb-2007 Time: 21:01:00
 */
public class AcyclicityConstraint extends BooleanFormula{

  private UserPredicate predicate;
  private int from, to;


  public AcyclicityConstraint(UserPredicate predicate) {
    this.predicate = predicate;
    from = 0;
    to = 1;
  }


  public AcyclicityConstraint(UserPredicate predicate, int from, int to) {
    this.predicate = predicate;
    this.from = from;
    this.to = to;
  }

  public UserPredicate getPredicate() {
    return predicate;
  }


  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public void acceptBooleanFormulaVisitor(BooleanFormulaVisitor visitor) {
    visitor.visitAcyclicityConstraint(this);
  }
}
