package thebeast.nod.identifier;

/**
 * @author Sebastian Riedel
 */
public interface ElementIdentifier extends Identifier {
    Identifier qualifier();
    int index();
}
