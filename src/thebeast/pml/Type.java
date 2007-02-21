package thebeast.pml;

import thebeast.nod.NoDServer;
import thebeast.nod.value.CategoricalValue;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.Value;
import thebeast.pml.term.CategoricalConstant;
import thebeast.pml.term.Constant;
import thebeast.pml.term.IntConstant;
import thebeast.pml.term.DoubleConstant;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:43:08
 */
public class Type {


  public enum Class {
    INT, POSITIVE_INT, NEGATIVE_INT,
    DOUBLE, POSITIVE_DOUBLE, NEGATIVE_DOUBLE,
    CATEGORICAL, CATEGORICAL_UNKNOWN
  }

  public final static String UNKNOWN = "-UNKNOWN-";

  public static Type INT, DOUBLE, POS_INT, NEG_INT, POS_DOUBLE, NEG_DOUBLE;

  static {
    NoDServer nodServer = TheBeast.getInstance().getNodServer();
    INT = new Type("Int", Type.Class.INT, nodServer.typeFactory().intType());
    DOUBLE = new Type("Double", Type.Class.DOUBLE, nodServer.typeFactory().doubleType());
    POS_INT = new Type("Int+", Type.Class.POSITIVE_INT, nodServer.typeFactory().intType());
    POS_DOUBLE = new Type("Double+", Type.Class.POSITIVE_DOUBLE, nodServer.typeFactory().doubleType());
    NEG_INT = new Type("Int-", Type.Class.NEGATIVE_INT, nodServer.typeFactory().intType());
    NEG_DOUBLE = new Type("Double-", Type.Class.NEGATIVE_DOUBLE, nodServer.typeFactory().doubleType());
  }

  private String name;
  private thebeast.nod.type.Type nodType;
  private Class typeClass;
  private HashSet<String> constants;

  public Type(String name, Class typeClass, thebeast.nod.type.Type nodType) {
    this.name = name;
    this.nodType = nodType;
    this.typeClass = typeClass;
  }

  public Type(String name, List<String> constants, boolean withUnknowns) {
    this.typeClass = withUnknowns ? Class.CATEGORICAL_UNKNOWN : Class.CATEGORICAL;
    Collections.sort(constants);
    NoDServer nodServer = TheBeast.getInstance().getNodServer();
    this.nodType = nodServer.typeFactory().createCategoricalType(
            nodServer.identifierFactory().createName(name), withUnknowns, constants);
    this.name = name;
    this.constants = new HashSet<String>(constants);
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return name;
  }


  public Set<String> getConstants() {
    return Collections.unmodifiableSet(constants);
  }

  public boolean isNumeric() {
    switch (typeClass) {
      case POSITIVE_INT:
      case NEGATIVE_INT:
      case POSITIVE_DOUBLE:
      case NEGATIVE_DOUBLE:
      case DOUBLE:
      case INT:
        return true;
    }
    return false;
  }


  public Constant toConstant(Value value) {
    switch (typeClass) {
      case CATEGORICAL_UNKNOWN:
      case CATEGORICAL:
        String name = ((CategoricalValue) value).representation();
        return new CategoricalConstant(this, name);
      case POSITIVE_INT:
      case NEGATIVE_INT:
      case INT:
        int integer = ((IntValue) value).getInt();
        return new IntConstant(this, integer);
    }
    return null;
  }

    public Constant toConstant(Object value) {
    switch (typeClass) {
      case CATEGORICAL_UNKNOWN:
      case CATEGORICAL:
        return new CategoricalConstant(this, (String) value);
      case POSITIVE_INT:
      case NEGATIVE_INT:
      case INT:
        return new IntConstant(this, (Integer)value);
      case POSITIVE_DOUBLE:
      case NEGATIVE_DOUBLE:
      case DOUBLE:
        return new DoubleConstant(this, (Double)value);
    }
    return null;
  }

  public thebeast.nod.type.Type getNodType() {
    return nodType;
  }

  public Class getTypeClass() {
    return typeClass;
  }

  public Constant getConstant(String name) {
    switch (typeClass) {
      case CATEGORICAL:
        if (constants.contains("\"" + name + "\""))
          return new CategoricalConstant(this, "\"" + name + "\"");
        if (!constants.contains(name)) throw new NotInTypeException(name, this);
        return new CategoricalConstant(this, name);
      case CATEGORICAL_UNKNOWN:
        if (constants.contains("\"" + name + "\""))
          return new CategoricalConstant(this, "\"" + name + "\"");
        return new CategoricalConstant(this, name);
      case INT:
        return new IntConstant(this, Integer.valueOf(name));
      case POSITIVE_INT:
        if (Integer.valueOf(name) < 0) throw new NotInTypeException(name, this);
        return new IntConstant(this, Integer.valueOf(name));
      case NEGATIVE_INT:
        if (Integer.valueOf(name) > 0) throw new NotInTypeException(name, this);
        return new IntConstant(this, Integer.valueOf(name));
    }
    return null;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Type type = (Type) o;

    return name.equals(type.name) && nodType.equals(type.nodType) && typeClass == type.typeClass;

  }

  public int hashCode() {
    int result;
    result = name.hashCode();
    result = 31 * result + nodType.hashCode();
    result = 31 * result + typeClass.hashCode();
    return result;
  }
}
