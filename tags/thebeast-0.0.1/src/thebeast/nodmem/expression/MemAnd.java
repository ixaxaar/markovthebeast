package thebeast.nodmem.expression;

import thebeast.nod.type.BoolType;
import thebeast.nod.expression.And;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.BoolExpression;
import thebeast.nodmem.type.MemBoolType;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:50:53
 */
public class MemAnd extends AbstractMemExpression<BoolType> implements And {

  private List<BoolExpression> arguments;

  protected MemAnd(List<BoolExpression> arguments) {
    super(MemBoolType.BOOL);
    this.arguments = new ArrayList<BoolExpression>(arguments);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitAnd(this);
  }

  public List<BoolExpression> arguments() {
    return arguments;
  }
}
