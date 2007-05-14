package thebeast.nod.value;

import thebeast.nod.type.RelationType;
import thebeast.nod.type.ArrayType;

import java.util.Iterator;

/**
 * @author Sebastian Riedel
 */
public interface ArrayValue extends Iterable<Value>, Value<ArrayType> {
  int size();

  Value element(int index);

  IntValue intElement(int index);

  DoubleValue doubleElement(int index);

  CategoricalValue categoricalElement(int index);


  BoolValue boolElement(int index);
}
