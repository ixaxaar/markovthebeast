package thebeast.nodmem.type;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.*;
import thebeast.nodmem.identifier.MemName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemTypeFactory implements TypeFactory {

  private HashMap<Name, Type> id2type = new HashMap<Name, Type>();
  private LinkedList<Type> types = new LinkedList<Type>();
  private IntType intType = new MemIntType(new MemName("int"), Integer.MIN_VALUE, Integer.MAX_VALUE);
  private DoubleType doubleType = new MemDoubleType(new MemName("double"), Double.MIN_VALUE, Double.MAX_VALUE);
  private BoolType boolType = MemBoolType.BOOL;
  private HashMap<Heading,TupleType> tupleTypes = new HashMap<Heading, TupleType>();
  private HashMap<Heading,RelationType> relationTypes = new HashMap<Heading, RelationType>();
  private HashMap<List<Attribute>, Heading> headings = new HashMap<List<Attribute>, Heading>();
  private HashMap<Attribute, Attribute> attributes = new HashMap<Attribute, Attribute>();

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

  public Heading createHeadingFromAttributes(List<Attribute> attributes) {
    Heading heading = headings.get(attributes);
    if (heading != null) return heading;
    headings.put(attributes, heading);
    return new MemHeading(attributes, 1);
  }

  public Heading createHeading(Attribute... attributes) {
    return createHeadingFromAttributes(Arrays.asList(attributes));
  }

  public TupleType createTupleType(Heading heading) {
    MemTupleType old = (MemTupleType) tupleTypes.get(heading);
    if (old!=null) return old;
    MemTupleType type = new MemTupleType((MemHeading) heading);
    types.add(type);
    tupleTypes.put(heading, type);
    return type;
  }

  public RelationType createRelationType(Heading heading) {
    RelationType type = relationTypes.get(heading);
    if (type != null) return type;
    MemRelationType memRelationType = new MemRelationType((MemHeading) heading);
    relationTypes.put(heading,memRelationType);
    return memRelationType;
  }

  public Attribute createAttribute(String name, Type type) {
    MemAttribute attribute = new MemAttribute(name, type);
    Attribute old = attributes.get(attribute);
    if (old != null) return old;
    attributes.put(attribute, attribute);
    return attribute;
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
