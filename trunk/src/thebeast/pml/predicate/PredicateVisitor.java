package thebeast.pml.predicate;

import thebeast.pml.UserPredicate;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:16:27
 */
public interface PredicateVisitor {
  void visitUserPredicate(UserPredicate userPredicate);

  void visitEquals(Equals equals);

  void visitIntLEQ(IntLEQ intLEQ);

  void visitNotEquals(NotEquals notEquals);

  void visitIntLT(IntLT intLT);

  void visitIntGT(IntGT intGT);

  void visitIntGEQ(IntGEQ intGEQ);

 
}
