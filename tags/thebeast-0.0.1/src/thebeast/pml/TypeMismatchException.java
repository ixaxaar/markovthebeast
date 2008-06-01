package thebeast.pml;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 12:54:08
 */
public class TypeMismatchException extends RuntimeException {

  private Type expected,actual;

  public TypeMismatchException(String message, Type expected, Type actual) {
    super(message + "; expected: " + expected.toString() + ", actual: " + actual.toString());
    this.expected = expected;
    this.actual = actual;
  }


  public Type getActual() {
    return actual;
  }

  public Type getExpected() {
    return expected;
  }
}
