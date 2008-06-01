package thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public class NoSuchPropertyException extends RuntimeException {
  private PropertyName name;


  public NoSuchPropertyException(PropertyName name) {
    super("Object " + name.getHead() + " does not have a property " + name.getTail());
    this.name = name;
  }


  public PropertyName getName() {
    return name;
  }
}
