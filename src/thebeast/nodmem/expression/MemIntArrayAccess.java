package thebeast.nodmem.expression;

import thebeast.nod.expression.ArrayExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntArrayAccess;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.type.IntType;

/**
 * @author Sebastian Riedel
 */
public class MemIntArrayAccess extends MemArrayAccess<IntType> implements IntArrayAccess {
  public MemIntArrayAccess(ArrayExpression array, IntExpression index) {
    super((IntType) array.type().instanceType(), array, index);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntArrayAccess(this);
  }
}
