package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.expression.IntPostIncrement;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.variable.IntVariable;
import thebeast.nodmem.type.MemIntType;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 18:59:23
 */
public class MemIntPostIncrement extends AbstractMemExpression<IntType> implements IntPostIncrement {

  private IntVariable variable;

  public MemIntPostIncrement(IntVariable variable) {
    super(MemIntType.INT);
    this.variable = variable;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntPostIncrement(this);
  }

  public IntVariable variable() {
    return variable;
  }
}
