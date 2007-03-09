package thebeast.pml;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Feb-2007 Time: 20:01:18
 */
public class PropertyName {

  private PropertyName tail;
  private String head;
  private List<Object> arguments;

  public PropertyName(String head, PropertyName tail) {
    this.head = head;
    this.tail = tail;
    arguments = null;
  }

  public boolean hasArguments(){
    return arguments != null;
  }

  public PropertyName(String head, PropertyName tail, List<Object> arguments) {
    this.head = head;
    this.tail = tail;
    this.arguments = arguments != null ? new ArrayList<Object>(arguments) : null;
  }


  public List<Object> getArguments() {
    return arguments;
  }

  public String getHead() {
    return head;
  }

  public PropertyName getTail() {
    return tail;
  }

  public boolean isTerminal(){
    return tail == null;
  }

  public String toString(){
    return tail == null ? head : head + "." + tail; 
  }

  public static Object getProperty(HasProperties of, PropertyName name){
    if (name == null) return of;
    else return of.getProperty(name);
  }

}
