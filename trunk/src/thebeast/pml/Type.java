package thebeast.pml;

import thebeast.nod.NoDServer;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.value.CategoricalValue;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.Value;
import thebeast.nod.value.BoolValue;
import thebeast.pml.term.*;

import java.util.*;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:43:08
 */
public class Type {
  public static Type BOOL;

  private static int typeCount = 0;


  public enum Class {
    INT, POSITIVE_INT, NEGATIVE_INT,
    DOUBLE, POSITIVE_DOUBLE, NEGATIVE_DOUBLE,
    CATEGORICAL, CATEGORICAL_UNKNOWN, BOOL
  }

  public final static String UNKNOWN = "-UNKNOWN-";

  public static Type INT, DOUBLE, POS_INT, NEG_INT, POS_DOUBLE, NEG_DOUBLE, SINGLETON;

  static {
    NoDServer nodServer = TheBeast.getInstance().getNodServer();
    INT = new Type("Int", Type.Class.INT, nodServer.typeFactory().intType());
    DOUBLE = new Type("Double", Type.Class.DOUBLE, nodServer.typeFactory().doubleType());
    POS_INT = new Type("Int+", Type.Class.POSITIVE_INT, nodServer.typeFactory().intType());
    POS_DOUBLE = new Type("Double+", Type.Class.POSITIVE_DOUBLE, nodServer.typeFactory().doubleType());
    NEG_INT = new Type("Int-", Type.Class.NEGATIVE_INT, nodServer.typeFactory().intType());
    NEG_DOUBLE = new Type("Double-", Type.Class.NEGATIVE_DOUBLE, nodServer.typeFactory().doubleType());
    BOOL = new Type("Bool", Class.BOOL, nodServer.typeFactory().boolType());
    SINGLETON = new Type("Singleton", false, "Singleton");
  }

  private String name;
  private thebeast.nod.type.Type nodType;
  private Class typeClass;
  //private HashSet<String> constants;

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
            nodServer.identifierFactory().createName(name + typeCount++), withUnknowns, constants);
    this.name = name;
  }

  public Type(String name, boolean withUnknowns, String ... constants){
    this(name, Arrays.asList(constants),withUnknowns);
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return name;
  }


  public Set<String> getConstants() {
    HashSet<String> result = new HashSet<String>();
    CategoricalType type = ((CategoricalType) nodType);
    for (CategoricalValue value : type.values()){
      result.add(value.representation());
    }
    return result;
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

  public boolean inherits(Type superType){
    if (this.equals(superType)) return true;
    if (superType.getTypeClass() == Class.DOUBLE &&
            (getTypeClass() == Class.POSITIVE_DOUBLE || getTypeClass() == Class.NEGATIVE_DOUBLE))
      return true;
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
      case BOOL:
        return new BoolConstant(((BoolValue)value).getBool());
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
        return new IntConstant(this, (Integer) value);
      case POSITIVE_DOUBLE:
      case NEGATIVE_DOUBLE:
      case DOUBLE:
        return new DoubleConstant(this, (Double) value);
      case BOOL:
        return new BoolConstant(this, (Boolean)value);
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
        CategoricalType type = (CategoricalType) nodType;
        if (type.contains("\"" + name + "\""))
          return new CategoricalConstant(this, "\"" + name + "\"");
        if (!type.contains(name)) throw new NotInTypeException(name, this);
        return new CategoricalConstant(this, name);
      case CATEGORICAL_UNKNOWN:
        type = (CategoricalType) nodType;
        if (type.contains("\"" + name + "\""))
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
      case BOOL:
        return new BoolConstant("true".equals(name));
    }
    return null;
  }

  public Object getObject(String name) {
    switch (typeClass) {
      case CATEGORICAL:
        CategoricalType type = (CategoricalType) nodType;
        if (type.contains("\"" + name + "\""))
          return "\"" + name + "\"";
        if (!type.contains(name)) throw new NotInTypeException(name, this);
        return name;
      case CATEGORICAL_UNKNOWN:
        type = (CategoricalType) nodType;
        if (type.contains("\"" + name + "\""))
          return "\"" + name + "\"";
        return name;
      case INT:
        return Integer.valueOf(name);
      case POSITIVE_INT:
        if (Integer.valueOf(name) < 0) throw new NotInTypeException(name, this);
        return Integer.valueOf(name);
      case NEGATIVE_INT:
        if (Integer.valueOf(name) > 0) throw new NotInTypeException(name, this);
        return Integer.valueOf(name);
      case BOOL:
        return "true".equals(name);
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

  public BigInteger getSize() {
    switch (typeClass){
      case INT:
        return BigInteger.valueOf(Integer.MAX_VALUE).subtract(BigInteger.valueOf(Integer.MIN_VALUE));
      case POSITIVE_INT:
        return BigInteger.valueOf(Integer.MAX_VALUE);
      case NEGATIVE_INT:
        return BigInteger.valueOf(Integer.MAX_VALUE);
      case DOUBLE:
        return BigInteger.valueOf(Long.MAX_VALUE);
      case POSITIVE_DOUBLE:
        return BigInteger.valueOf(Long.MAX_VALUE).divide(BigInteger.valueOf(2));
      case NEGATIVE_DOUBLE:
        return BigInteger.valueOf(Long.MAX_VALUE).divide(BigInteger.valueOf(2));
      case CATEGORICAL:
        CategoricalType type = (CategoricalType) nodType;
        return BigInteger.valueOf(type.values().size());
      case CATEGORICAL_UNKNOWN:
        type = (CategoricalType) nodType;
        return BigInteger.valueOf(type.values().size()+1);
      case BOOL:
        return BigInteger.valueOf(2);
    }
    return null;
  }
  

}
