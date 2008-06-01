package thebeast.nod.type;

import thebeast.nod.type.Heading;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface RelationType extends SetType {

  Heading heading();

  List<KeyAttributes> candidateKeys();

  

}
