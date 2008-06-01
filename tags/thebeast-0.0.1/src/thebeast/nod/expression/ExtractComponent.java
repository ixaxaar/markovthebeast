package thebeast.nod.expression;

import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public interface ExtractComponent<T extends Type> extends Expression<T> {

  TupleExpression tuple();
  String name();

}
