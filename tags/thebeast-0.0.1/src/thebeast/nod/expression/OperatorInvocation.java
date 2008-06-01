package thebeast.nod.expression;

import thebeast.nod.type.Type;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface OperatorInvocation<R extends Type> extends Expression<R> {


  Operator<R> operator();

  List<Expression> args();


}
