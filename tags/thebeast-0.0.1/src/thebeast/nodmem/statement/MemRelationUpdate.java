package thebeast.nodmem.statement;

import thebeast.nod.statement.RelationUpdate;
import thebeast.nod.statement.AttributeAssign;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.expression.BoolExpression;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 18:43:44
 */
public class MemRelationUpdate implements RelationUpdate {

  private RelationVariable target;
  private ArrayList<AttributeAssign> attributeAssigns;
  private BoolExpression where;


  public MemRelationUpdate(RelationVariable target, BoolExpression where, List<AttributeAssign> attributeAssigns) {
    this.target = target;
    this.where = where;
    this.attributeAssigns = new ArrayList<AttributeAssign>(attributeAssigns);
  }

  public RelationVariable target() {
    return target;
  }

  public List<AttributeAssign> attributeAssigns() {
    return attributeAssigns;
  }

  public BoolExpression where() {
    return where;
  }

  public void acceptStatementVisitor(StatementVisitor visitor) {
    visitor.visitRelationUpdate(this);
  }
}
