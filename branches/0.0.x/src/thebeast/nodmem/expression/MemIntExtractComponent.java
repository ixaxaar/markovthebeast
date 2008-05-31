package thebeast.nodmem.expression;

import thebeast.nod.type.IntType;
import thebeast.nod.expression.*;

/**
 * @author Sebastian Riedel
 */
public class MemIntExtractComponent extends MemExtractComponent<IntType> implements IntExtractComponent {
  public MemIntExtractComponent(TupleExpression tuple, String name) {
    super((IntType) tuple.type().heading().getType(name), tuple, name);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntExtractComponent(this);
  }
}
