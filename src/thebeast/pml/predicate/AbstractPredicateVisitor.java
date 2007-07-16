package thebeast.pml.predicate;

import thebeast.pml.UserPredicate;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractPredicateVisitor implements PredicateVisitor {

  private boolean throwExceptions = false;

  public static class UnsupportedHostException extends RuntimeException {
    private Predicate host;

    public UnsupportedHostException(Predicate host) {
      super("Visitor does not support " + host);
      this.host = host;
    }

    public Predicate getHost() {
      return host;
    }
  }


  public void visitUserPredicate(UserPredicate userPredicate) {
    if (throwExceptions) throw new UnsupportedHostException(userPredicate);
  }

  public void visitEquals(Equals equals) {
    if (throwExceptions) throw new UnsupportedHostException(equals);
  }

  public void visitIntLEQ(IntLEQ intLEQ) {
    if (throwExceptions) throw new UnsupportedHostException(intLEQ);
  }

  public void visitNotEquals(NotEquals notEquals) {
    if (throwExceptions) throw new UnsupportedHostException(notEquals);
  }

  public void visitIntLT(IntLT intLT) {
    if (throwExceptions) throw new UnsupportedHostException(intLT);
  }

  public void visitIntGT(IntGT intGT) {
    if (throwExceptions) throw new UnsupportedHostException(intGT);
  }

  public void visitIntGEQ(IntGEQ intGEQ) {
    if (throwExceptions) throw new UnsupportedHostException(intGEQ);
  }

  
}
