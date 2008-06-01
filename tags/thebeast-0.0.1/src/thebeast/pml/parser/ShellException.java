package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Feb-2007 Time: 17:17:17
 */
public class ShellException extends RuntimeException{

  public ShellException(String message) {
    super(message);
  }

  public ShellException(String message, Throwable cause) {
    super(message, cause);
  }
}
