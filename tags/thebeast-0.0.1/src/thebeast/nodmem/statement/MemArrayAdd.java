package thebeast.nodmem.statement;

import thebeast.nod.expression.ArrayExpression;
import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.statement.ArrayAdd;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.variable.ArrayVariable;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 18:48:06
 */
public class MemArrayAdd implements ArrayAdd {

  private ArrayVariable variable;
  private ArrayExpression expression;
  private DoubleExpression scale;


  public MemArrayAdd(ArrayVariable variable, ArrayExpression expression, DoubleExpression scale) {
    this.variable = variable;
    this.expression = expression;
    this.scale = scale;
  }

  public ArrayVariable variable() {
    return variable;
  }

  public ArrayExpression argument() {
    return expression;
  }



  public DoubleExpression scale(){
    return scale;
  }

  public void acceptStatementVisitor(StatementVisitor visitor) {
    visitor.visitArrayAdd(this);
  }
}
