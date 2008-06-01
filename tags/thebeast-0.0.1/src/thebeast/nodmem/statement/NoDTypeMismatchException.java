package thebeast.nodmem.statement;

import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public class NoDTypeMismatchException extends RuntimeException {
  private Type expected, actual;
  private Object context;


  public NoDTypeMismatchException(Type expected, Type actual, Object context) {
    super("Expected " + expected + " but got " + actual + " in the context " + context);
    this.expected = expected;
    this.actual = actual;
    this.context = context;
  }


  public Type getExpected() {
    return expected;
  }

  public Type getActual() {
    return actual;
  }

  public Object getContext() {
    return context;
  }
}
