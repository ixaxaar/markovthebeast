package thebeast.nodmem.type;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.Type;
import thebeast.nod.identifier.Name;
import thebeast.util.Pair;

/**
 * @author Sebastian Riedel
 */
public class MemAttribute implements Attribute {

    private String name;
    private Type type;

    enum DataType {INT,DOUBLE,CHUNK}

    public MemAttribute(Pair<String, Type> attribute) {
        name = attribute.arg1;
        type = attribute.arg2;
    }

    public MemAttribute(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemAttribute attribute = (MemAttribute) o;

    return name.equals(attribute.name) && type.equals(attribute.type);

  }

  public int hashCode() {
    int result;
    result = name.hashCode();
    result = 31 * result + type.hashCode();
    return result;
  }
}
