package thebeast.nodmem.expression;

import thebeast.nod.type.BoolType;
import thebeast.nod.expression.BoolExpression;
import thebeast.nod.expression.Expression;
import thebeast.nodmem.type.MemBoolType;


/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:43:01
 */
public abstract class BinaryComparison<L extends Expression, R extends Expression>
        extends AbstractMemExpression<BoolType> implements BoolExpression {

  protected L expr1;
  protected R expr2;

  protected BinaryComparison(L expr1, R expr2) {
    super(MemBoolType.BOOL);
    this.expr1 = expr1;
    this.expr2 = expr2;
  }


  public L leftHandSide() {
    return expr1;
  }

  public R rightHandSide() {
    return expr2;
  }
}
