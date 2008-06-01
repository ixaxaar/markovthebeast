package thebeast.pml.formula;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Feb-2007 Time: 13:42:03
 */
public class AbstractAtomVisitor implements AtomVisitor {
  public void visitPredicateAtom(PredicateAtom predicateAtom) {

  }

  public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {

  }

  public void visitTrue(True aTrue) {

  }

  public void visitExists(Exists exists) {
    exists.toGEQConstraint().acceptAtomVisitor(this);
  }

  public void visitForall(Forall forall) {
    forall.toLEQConstraint().acceptAtomVisitor(this);
  }

  public void visitUndefinedWeight(UndefinedWeight undefinedWeight) {

  }
}
