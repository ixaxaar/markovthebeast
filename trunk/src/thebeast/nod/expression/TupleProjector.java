package thebeast.nod.expression;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.TupleType;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface TupleProjector extends Expression {

    Expression<TupleType> tupleExpression();
    List<Name> attributes();

  

}
