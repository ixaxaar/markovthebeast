package thebeast.nod.type;

import thebeast.nod.identifier.Name;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface Heading {

  List<Attribute> attributes();

  Type getType(String name);

  int getIndex(String name);

  List<String> getAttributeNames();

  Attribute attribute(String attributeName);
  
}
