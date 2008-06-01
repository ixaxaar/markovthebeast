package thebeast.pml;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 20:36:02
 */
public class NotInTypeException extends RuntimeException {

  private String name;
  private Type type;


  public NotInTypeException(String name, Type type) {
    super(name + " is not a member of " + type.getName());
    this.name = name;
    this.type = type;
  }


  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }
}
