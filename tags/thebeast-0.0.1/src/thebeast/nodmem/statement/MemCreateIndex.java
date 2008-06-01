package thebeast.nodmem.statement;

import thebeast.nod.statement.CreateIndex;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Jan-2007 Time: 16:13:17
 */
public class MemCreateIndex implements CreateIndex {

  private Index.Type indexType;
  private RelationVariable variable;
  private ArrayList<String> attributes;
  private String name;


  public MemCreateIndex(String name, Collection<String> attributes, Index.Type indexType, RelationVariable variable) {
    this.attributes = new ArrayList<String>(attributes);
    Collections.sort(this.attributes);
    this.indexType = indexType;
    this.variable = variable;
    this.name = name;
  }

  public String name() {
    return name;
  }

  public Index.Type indexType() {
    return indexType;
  }

  public RelationVariable variable() {
    return variable;
  }

  public List<String> attributes() {
    return attributes;
  }

  public void acceptStatementVisitor(StatementVisitor visitor) {
    visitor.visitCreateIndex(this);
  }
}
