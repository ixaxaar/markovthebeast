package thebeast.pml.term;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 10-Feb-2007 Time: 16:34:30
 */
public class AbstractTermVisitor implements TermVisitor {

  private boolean throwExceptions = false;

  public static class UnsupportedHostException extends RuntimeException {
    private Term host;

    public UnsupportedHostException(Term host) {
      super("Visitor does not support " + host);
      this.host = host;
    }

    public Term getHost() {
      return host;
    }
  }

  public AbstractTermVisitor(boolean throwExceptions) {
    this.throwExceptions = throwExceptions;
  }

  public AbstractTermVisitor() {
  }

  public void visitVariable(Variable variable) {
    if (throwExceptions) throw new UnsupportedHostException(variable);
  }

  public void visitFunctionApplication(FunctionApplication functionApplication) {
    if (throwExceptions) throw new UnsupportedHostException(functionApplication);

  }

  public void visitIntConstant(IntConstant intConstant) {
    if (throwExceptions) throw new UnsupportedHostException(intConstant);

  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    if (throwExceptions) throw new UnsupportedHostException(categoricalConstant);

  }

  public void visitDontCare(DontCare dontCare) {
    if (throwExceptions) throw new UnsupportedHostException(dontCare);
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    if (throwExceptions) throw new UnsupportedHostException(doubleConstant);
  }

  public void visitBinnedInt(BinnedInt binnedInt) {
    if (throwExceptions) throw new UnsupportedHostException(binnedInt);

  }
}
