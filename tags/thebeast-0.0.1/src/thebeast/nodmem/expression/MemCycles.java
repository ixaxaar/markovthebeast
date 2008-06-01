package thebeast.nodmem.expression;

import thebeast.nod.expression.Cycles;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.Attribute;
import thebeast.nodmem.type.MemAttribute;
import thebeast.nodmem.type.MemRelationType;
import thebeast.nodmem.type.MemHeading;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class MemCycles extends AbstractMemExpression<RelationType> implements Cycles {

  private RelationExpression graph;
  private String to, from;


  public MemCycles(RelationExpression graph, String from, String to) {
    super(determineType(graph.type()));
    this.graph = graph;
    this.from = from;
    this.to = to;

  }

  private static RelationType determineType(RelationType relationType) {
    MemAttribute attribute = new MemAttribute("cycle",relationType);
    LinkedList<Attribute> attributes = new LinkedList<Attribute>();
    attributes.add(attribute);
    MemHeading heading = new MemHeading(attributes, 0);
    return new MemRelationType(heading);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitCycles(this);
  }

  public RelationExpression graph() {
    return graph;
  }

  public String fromAttribute() {
    return from;
  }

  public String toAttribute() {
    return to;
  }
}
