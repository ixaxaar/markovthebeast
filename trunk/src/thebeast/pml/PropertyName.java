package thebeast.pml;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Feb-2007 Time: 20:01:18
 */
public class PropertyName {

  private PropertyName tail;
  private String head;


  public PropertyName(String head, PropertyName tail) {
    this.head = head;
    this.tail = tail;
  }


  public String getHead() {
    return head;
  }

  public PropertyName getTail() {
    return tail;
  }
}
