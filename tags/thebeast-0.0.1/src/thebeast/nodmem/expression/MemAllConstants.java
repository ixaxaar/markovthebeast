package thebeast.nodmem.expression;

import thebeast.nod.type.RelationType;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.type.Attribute;
import thebeast.nod.expression.AllConstants;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nodmem.type.MemAttribute;
import thebeast.nodmem.type.MemRelationType;
import thebeast.nodmem.type.MemHeading;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class MemAllConstants extends AbstractMemExpression<RelationType> implements AllConstants {

  private CategoricalType ofType;

  protected MemAllConstants(CategoricalType ofType) {
    super(determineType(ofType));
    this.ofType = ofType;
  }

  private static RelationType determineType(CategoricalType ofType) {
    MemAttribute attribute = new MemAttribute("value",ofType);
    LinkedList<MemAttribute> attributes = new LinkedList<MemAttribute>();
    attributes.add(attribute);
    MemHeading heading = new MemHeading(attributes,0);
    return new MemRelationType(heading);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitAllConstants(this);
  }

  public CategoricalType ofType() {
    return ofType;
  }
}
