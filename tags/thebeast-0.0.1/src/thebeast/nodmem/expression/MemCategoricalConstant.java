package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.CategoricalConstant;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.value.CategoricalValue;

/**
 * @author Sebastian Riedel
 */
public class MemCategoricalConstant extends AbstractMemConstant<CategoricalType,CategoricalValue>
        implements CategoricalConstant {

  private String representation;

  protected MemCategoricalConstant(CategoricalType type, String value) {
    super(type);
    if (type.index(value)==-1 && !type.unknowns())
      throw new RuntimeException(value + " is not a member of " + type);
    this.representation = value;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitCategoricalConstant(this);
  }

  public String representation() {
    return representation;
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemCategoricalConstant that = (MemCategoricalConstant) o;

    return representation.equals(that.representation);

  }

  public int hashCode() {
    return representation.hashCode();
  }
}
