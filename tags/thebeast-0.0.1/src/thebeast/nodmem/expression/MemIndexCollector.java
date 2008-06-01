package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IndexCollector;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.RelationType;
import thebeast.nodmem.type.*;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class MemIndexCollector extends AbstractMemExpression<RelationType> implements IndexCollector {

  private String groupAttribute;
  private RelationExpression grouped;
  private String indexAttribute;
  private String valueAttribute;

  public MemIndexCollector(String groupAttribute, RelationExpression grouped,
                           String indexAttribute, String valueAttribute) {
    super(determineType(indexAttribute,valueAttribute));
    this.groupAttribute = groupAttribute;
    this.grouped = grouped;
    this.indexAttribute = indexAttribute;
    this.valueAttribute = valueAttribute;
  }

  private static RelationType determineType(String indexAttribute, String valueAttribute) {
    MemAttribute indexAtt = new MemAttribute(indexAttribute, MemIntType.INT);
    MemAttribute valueAtt = new MemAttribute(valueAttribute, MemDoubleType.DOUBLE);
    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    attributes.add(indexAtt);
    attributes.add(valueAtt);
    MemHeading heading = new MemHeading(attributes, 0);
    return new MemRelationType(heading);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIndexCollector(this);
  }

  public RelationExpression grouped() {
    return grouped;
  }

  public String groupAttribute() {
    return groupAttribute;
  }

  public String indexAttribute() {
    return indexAttribute;
  }

   public String valueAttribute() {
    return valueAttribute;
  }
}
