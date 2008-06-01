package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.*;

/**
 * @author Sebastian Riedel
 */
public class MemDoubleArrayAccess extends MemArrayAccess<DoubleType> implements DoubleArrayAccess {
  public MemDoubleArrayAccess(ArrayExpression array, IntExpression index) {
    super((DoubleType) array.type().instanceType(), array, index);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleArrayAccess(this);
  }
}
