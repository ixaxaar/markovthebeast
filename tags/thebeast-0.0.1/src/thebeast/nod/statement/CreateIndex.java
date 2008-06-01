package thebeast.nod.statement;

import thebeast.nod.type.Attribute;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.identifier.Name;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Jan-2007 Time: 15:57:46
 */
public interface CreateIndex extends Statement {

  String name();
  Index.Type indexType();
  RelationVariable variable();
  List<String> attributes();

  
  
}
