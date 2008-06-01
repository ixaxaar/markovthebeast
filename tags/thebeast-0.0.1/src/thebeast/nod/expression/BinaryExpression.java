package thebeast.nod.expression;

import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public interface BinaryExpression<L extends Expression, R extends Expression, T extends Type> extends Expression<T> {
  L leftHandSide();
  R rightHandSide();
}
