package thebeast.nodmem.statement;

import thebeast.nod.statement.ArrayAppend;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.expression.ArrayExpression;
import thebeast.nod.variable.ArrayVariable;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 18:48:06
 */
public class MemArrayAppend implements ArrayAppend {

  private ArrayVariable variable;
  private ArrayExpression expression;


  public MemArrayAppend(ArrayVariable variable, ArrayExpression expression) {
    this.variable = variable;
    this.expression = expression;
  }

  public ArrayVariable variable() {
    return variable;
  }

  public ArrayExpression expression() {
    return expression;
  }

  public void acceptStatementVisitor(StatementVisitor visitor) {
    visitor.visitArrayAppend(this);
  }
}
