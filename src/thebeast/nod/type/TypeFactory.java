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

  Heading createHeading(List<Pair<String, Type>> attributes);

  Heading createHeadingFromAttributes(List<Attribute> attributes);

  TupleType createTupleType(Heading heading);

  RelationType createRelationType(Heading heading);

  RelationType createRelationType(Heading heading, Attribute... primaryKey);

  Attribute createAttribute(String name, Type type);

  List<Type> types();

  CategoricalType categoricalType(Name id);

  IntType intType(Name id);

  CategoricalType createCategoricalType(Name name, boolean unknowns, List<String> representations);
}
