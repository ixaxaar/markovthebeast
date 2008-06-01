package thebeast.nod.identifier;

/**
 * @author Sebastian Riedel
 */
public interface Identifier {
    void acceptIdentifierVisitor(IdentifierVisitor visitor);
}
