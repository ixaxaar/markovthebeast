package thebeast.nod.expression;

import thebeast.nod.identifier.Name;
import thebeast.nod.identifier.Identifier;
import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public interface VariableReference<T extends Type> extends Expression<T> {

    Identifier identifier();
}
