package thebeast.nod.expression;

import thebeast.nod.type.Type;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface NAryExpression<A extends Expression, T extends Type> extends Expression<T>  {
  List<A> arguments();
}
