package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.type.DoubleType;
import thebeast.nod.expression.IntExtractComponent;
import thebeast.nod.expression.TupleExpression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.DoubleExtractComponent;

/**
 * @author Sebastian Riedel
 */
public class MemDoubleExtractComponent extends MemExtractComponent<DoubleType> implements DoubleExtractComponent {
  public MemDoubleExtractComponent(TupleExpression tuple, String name) {
    super((DoubleType) tuple.type().heading().getType(name), tuple, name);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitDoubleExtractComponent(this);
  }
}
