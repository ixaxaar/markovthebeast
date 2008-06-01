package thebeast.nod.expression;

import thebeast.nod.type.TupleType;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface TupleSelector extends TupleExpression {
  List<TupleComponent> components();
  List<Expression> expressions();
}
