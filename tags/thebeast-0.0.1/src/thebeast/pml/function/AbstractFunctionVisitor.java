package thebeast.pml.function;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 10-Feb-2007 Time: 16:37:51
 */
public class AbstractFunctionVisitor implements FunctionVisitor {

  private boolean throwsException = false;

  public static class UnsupportedHostException extends RuntimeException {
    private Function host;

    public UnsupportedHostException(Function host) {
      super("Visitor does not support " + host);
      this.host = host;
    }

    public Function getHost() {
      return host;
    }
  }


  public AbstractFunctionVisitor(boolean throwsException) {
    this.throwsException = throwsException;
  }


  public AbstractFunctionVisitor() {
  }

  public void visitWeightFunction(WeightFunction weightFunction) {
    if (throwsException) throw new UnsupportedHostException(weightFunction);
  }

  public void visitIntAdd(IntAdd intAdd) {
    if (throwsException) throw new UnsupportedHostException(intAdd);
  }

  public void visitIntMinus(IntMinus intMinus) {
    if (throwsException) throw new UnsupportedHostException(intMinus);
  }

  public void visitIntMin(IntMin intMin) {
    if (throwsException) throw new UnsupportedHostException(intMin);
  }

  public void visitIntMax(IntMax intMax) {
    if (throwsException) throw new UnsupportedHostException(intMax);
  }

  public void visitDoubleProduct(DoubleProduct doubleProduct) {
    if (throwsException) throw new UnsupportedHostException(doubleProduct);
  }

  public void visitDoubleCast(DoubleCast doubleCast) {
    if (throwsException) throw new UnsupportedHostException(doubleCast);
  }

  public void visitDoubleAbs(DoubleAbs doubleAbs) {
    if (throwsException) throw new UnsupportedHostException(doubleAbs);
  }

  public void visitDoubleAdd(DoubleAdd doubleAdd) {
    if (throwsException) throw new UnsupportedHostException(doubleAdd);
  }

  public void visitDoubleMinus(DoubleMinus doubleMinus) {
    if (throwsException) throw new UnsupportedHostException(doubleMinus);
  }
}
