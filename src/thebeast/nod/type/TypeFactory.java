package thebeast.nod.type;

import thebeast.nod.identifier.Name;
import thebeast.util.Pair;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface TypeFactory {

  IntType intType();

  DoubleType doubleType();

  BoolType boolType();

  IntType createIntType(Name name, int from, int to);

  DoubleType createDoubleType(Name name, double from, double to);

  BoolType createBoolType();

  CategoricalType createCategoricalType(Name name, List<String> representations);

  Heading createHeadingFromAttributes(List<Attribute> attributes);

  Heading createHeading(Attribute ... attributes);

  TupleType createTupleType(Heading heading);

  ArrayType createArrayType(Type instanceType);

  RelationType createRelationType(Heading heading);

  Attribute createAttribute(String name, Type type);

  List<Type> types();

  CategoricalType categoricalType(Name id);

  IntType intType(Name id);

  CategoricalType createCategoricalType(Name name, boolean unknowns, List<String> representations);
}
