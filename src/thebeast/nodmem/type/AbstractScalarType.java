package thebeast.nodmem.type;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.ScalarType;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractScalarType extends AbstractMemType implements ScalarType {

  protected Name name;

  protected AbstractScalarType(Name name, DataType dataType) {
    super(dataType);
    if (name == null) throw new RuntimeException("Please define a type name");
    this.name = name;

  }


  public Name name() {
    return name;
  }

  public String toString(){
    return name.name();
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AbstractScalarType that = (AbstractScalarType) o;

    if (!name.equals(that.name)) return false;

    return true;
  }

  public int hashCode() {
    return name.hashCode();
  }
}
