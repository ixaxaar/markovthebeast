package thebeast.nod.expression;

import thebeast.nod.type.Type;
import thebeast.nod.variable.Variable;
import thebeast.nod.expression.Expression;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface Operator<R extends Type> {
  List<Variable> args();
  Expression<R> result();
  String name();

}
