package thebeast.nod.expression;

import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public interface ArrayAccess<T extends Type> extends Expression<T> {

  ArrayExpression array();
  IntExpression index();

}
