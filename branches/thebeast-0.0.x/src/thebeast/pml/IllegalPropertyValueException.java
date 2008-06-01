package thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public class IllegalPropertyValueException extends RuntimeException {
  private PropertyName name;
  private Object value;


  public IllegalPropertyValueException(PropertyName name, Object value) {
    super(value + " is an illegal argument for property " + name);
    this.name = name;
    this.value = value;
  }


  public Object getValue() {
    return value;
  }

  public PropertyName getName() {
    return name;
  }
}
