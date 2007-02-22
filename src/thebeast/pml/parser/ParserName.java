package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Feb-2007 Time: 19:50:53
 */
public class ParserName {

  public final ParserName tail;
  public final String head;

  public ParserName(String head, ParserName tail) {
    this.tail = tail;
    this.head = head;
  }

  public String toString() {
    return tail == null ? head : head + "." + tail;
  }
}
