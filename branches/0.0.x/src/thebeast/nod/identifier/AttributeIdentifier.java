package thebeast.nod.identifier;

/**
 * @author Sebastian Riedel
 */
public interface AttributeIdentifier extends Identifier {
    Identifier qualifier();
    Name attributeName();
}
