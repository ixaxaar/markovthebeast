package thebeast.nod.statement;

import thebeast.nod.variable.ArrayVariable;
import thebeast.nod.expression.ArrayExpression;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 18:47:24
 */
public interface ArrayAppend extends Statement {

  ArrayVariable variable();
  ArrayExpression expression();
}
