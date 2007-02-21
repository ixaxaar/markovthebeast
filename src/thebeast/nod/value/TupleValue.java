package thebeast.nod.value;

import thebeast.nod.value.Value;
import thebeast.nod.type.TupleType;
import thebeast.nod.identifier.Name;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface TupleValue extends Value<TupleType> {

  List<? extends Value> values();

  Value element(String name);

  Value element(int index);

  IntValue intElement(String name);

  IntValue intElement(int index);

  DoubleValue doubleElement(int index);

  DoubleValue doubleElement(String name);

  CategoricalValue categoricalElement(int index);

  RelationValue relationElement(int index);

  RelationValue relationElement(String name);

  int size();

}
