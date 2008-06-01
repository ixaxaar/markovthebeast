package thebeast.nod.expression;

import thebeast.nod.identifier.Name;
import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public interface NameIntroduction<T extends Type> {
    Expression<T> expression();
    Name name();
}
