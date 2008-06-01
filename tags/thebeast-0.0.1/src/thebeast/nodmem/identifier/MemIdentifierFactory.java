package thebeast.nodmem.identifier;

import thebeast.nod.identifier.IdentifierFactory;
import thebeast.nod.identifier.Name;

/**
 * @author Sebastian Riedel
 */
public class MemIdentifierFactory implements IdentifierFactory {
    public Name createName(String name) {
        return new MemName(name);
    }
}
