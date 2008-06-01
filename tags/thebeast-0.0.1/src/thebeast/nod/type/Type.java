package thebeast.nod.type;

/**
 * @author Sebastian Riedel
 */
public interface Type {

    void acceptTypeVisitor(TypeVisitor visitor);

}
