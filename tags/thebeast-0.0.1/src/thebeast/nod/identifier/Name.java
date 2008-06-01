package thebeast.nod.identifier;

/**
 * @author Sebastian Riedel
 */
public interface Name extends Identifier {

    String name();
    Name qualifier();

}
