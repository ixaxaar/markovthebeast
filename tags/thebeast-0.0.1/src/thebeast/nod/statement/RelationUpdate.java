package thebeast.nod.statement;

import thebeast.nod.expression.BoolExpression;
import thebeast.nod.variable.RelationVariable;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 18:42:13
 */
public interface RelationUpdate extends Statement{

  RelationVariable target();
  List<AttributeAssign> attributeAssigns();
  BoolExpression where();


}
