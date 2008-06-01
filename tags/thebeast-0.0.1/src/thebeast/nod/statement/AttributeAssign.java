package thebeast.nod.statement;

import thebeast.nod.expression.Expression;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 18:41:30
 */
public interface AttributeAssign {

  String attributeName();
  Expression expression();

}
