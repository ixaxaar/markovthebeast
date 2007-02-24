package thebeast.nod.variable;

import thebeast.nod.type.Attribute;
import thebeast.nod.identifier.Name;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Jan-2007 Time: 15:58:48
 */
public interface Index extends Comparable<Index> {

  enum Type {HASH,SEQ}

  RelationVariable variable();
  List<String> attributes();
  Type indexType();

}
