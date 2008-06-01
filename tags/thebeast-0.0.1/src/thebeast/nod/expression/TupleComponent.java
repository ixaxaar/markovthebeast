package thebeast.nod.expression;

import thebeast.nod.identifier.Name;

/**
 * @author Sebastian Riedel
 */
public interface TupleComponent {

    String name();
    Expression expression();

}
