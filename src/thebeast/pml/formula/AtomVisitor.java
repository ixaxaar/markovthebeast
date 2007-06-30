package thebeast.pml.formula;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 05-Feb-2007 Time: 17:16:20
 */
public interface AtomVisitor {
  void visitPredicateAtom(PredicateAtom predicateAtom);

  void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint);

  void visitTrue(True aTrue);

  void visitExists(Exists exists);

  void visitForall(Forall forall);
}
