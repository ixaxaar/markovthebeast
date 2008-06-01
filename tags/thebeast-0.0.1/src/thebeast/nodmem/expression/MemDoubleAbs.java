package thebeast.nodmem.expression;

import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemDoubleAbs extends AbstractMemExpression<DoubleType> implements DoubleAbs {

  private DoubleExpression argument;

  public MemDoubleAbs(DoubleType type, DoubleExpression argument) {
    super(type);
    this.argument = argument;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleAbs(this);
  }


  public DoubleExpression argument() {
    return argument;
  }
}
