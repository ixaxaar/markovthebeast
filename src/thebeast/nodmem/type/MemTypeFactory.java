package thebeast.nodmem.type;

import thebeast.nod.type.*;
import thebeast.nod.identifier.Name;
import thebeast.util.Pair;
import thebeast.nodmem.identifier.MemName;

import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class MemTypeFactory implements TypeFactory {

  private HashMap<Name, Type> id2type = new HashMap<Name, Type>();
  private LinkedList<Type> types = new LinkedList<Type>();
  private IntType intType = new MemIntType(new MemName("int"), Integer.MIN_VALUE, Integer.MAX_VALUE);
  private DoubleType doubleType = new MemDoubleType(new MemName("double"), Double.MIN_VALUE, Double.MAX_VALUE);
  private BoolType boolType = MemBoolType.BOOL;

  public IntType intType() {
    return intType;
  }

  public DoubleType doubleType() {
    return doubleType;
  }

  public BoolType boolType() {
    return boolType;
  }

  public IntType createIntType(Name name, int from, int to) {
    MemIntType memIntType = new MemIntType(name, from, to);
    id2type.put(name, memIntType);
    types.add(memIntType);
    return memIntType;
  }

  public DoubleType createDoubleType(Name name, double from, double to) {
    MemDoubleType memDoubleType = new MemDoubleType(name, from, to);
    id2type.put(name, memDoubleType);
    types.add(memDoubleType);
    return memDoubleType;

  }

  public BoolType createBoolType() {
    return null;
  }

  public CategoricalType createCategoricalType(Name name, List<String> representations) {
    MemCategoricalType type = new MemCategoricalType(name, false, representations);
    id2type.put(name, type);
    types.add(type);
    return type;
  }

   public CategoricalType createCategoricalType(Name name, boolean unknowns, List<String> representations) {
    MemCategoricalType type = new MemCategoricalType(name, unknowns, representations);
    id2type.put(name, type);
    types.add(type);
    return type;
  }
  public Heading createHeading(List<Pair<String, Type>> attributes) {
    return new MemHeading(attributes);
  }

  public Heading createHeadingFromAttributes(List<Attribute> attributes) {
    return new MemHeading(attributes, 1);
  }

  public Heading createHeading(Attribute... attributes) {
    return createHeadingFromAttributes(Arrays.asList(attributes));
  }

  public TupleType createTupleType(Heading heading) {
    MemTupleType type = new MemTupleType((MemHeading) heading);
    types.add(type);
    return type;
  }

  public RelationType createRelationType(Heading heading) {
    return new MemRelationType((MemHeading) heading);
  }

  public RelationType createRelationType(Heading heading, Attribute... primaryKey) {
    MemKeyAttributes attributes = new MemKeyAttributes((MemHeading) heading, Arrays.asList(primaryKey));
    LinkedList<KeyAttributes> candidates = new LinkedList<KeyAttributes>();
    candidates.add(attributes);
    return new MemRelationType((MemHeading) heading, candidates);
  }

  public RelationType createRelationType(Heading heading, List<KeyAttributes> candidateKeys) {
    return new MemRelationType((MemHeading) heading, candidateKeys);
  }

  public Attribute createAttribute(String name, Type type) {
    return new MemAttribute(name, type);
  }


  public List<Type> types() {
    return types;
  }

  public CategoricalType categoricalType(Name id) {
    return (CategoricalType) id2type.get(id);
  }

  public IntType intType(Name id) {
    return (IntType) id2type.get(id);
  }

  public ArrayType createArrayType(Type instanceType) {
    return new MemArrayType(instanceType);
  }
}
