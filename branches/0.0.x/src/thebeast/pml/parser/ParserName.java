package thebeast.pml.parser;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Feb-2007 Time: 19:50:53
 */
public class ParserName {

  public final ParserName tail;
  public final String head;
  public final List<Object> arguments;

  public ParserName(String head, ParserName tail) {
    this.tail = tail;
    this.head = head;
    arguments = null;
  }

  public ParserName(String head, ParserName tail, List<Object> arguments) {
    this.head = head;
    this.tail = tail;
    this.arguments = arguments;
  }

  public String toString() {
    return (tail == null ? head : head + "." + tail)+  (arguments != null ? arguments.toString() : "");
  }
}
