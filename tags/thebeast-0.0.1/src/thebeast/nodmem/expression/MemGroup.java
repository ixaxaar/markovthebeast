package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.Group;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.RelationType;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemRelationType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 19:59:42
 */
public class MemGroup extends AbstractMemExpression<RelationType> implements Group {

  private RelationExpression relation;
  private ArrayList<String> attributes;
  private String as;


  public MemGroup(RelationExpression relation, List<String> attributes, String as) {
    super(determineType(relation,attributes,as));
    this.relation = relation;
    this.attributes = new ArrayList<String>(attributes);
    this.as = as;
  }

  private static RelationType determineType(RelationExpression relation, List<String> attributes, String as) {
    HashSet<String> toGroup = new HashSet<String>(attributes);
    LinkedList<Attribute> newAttributes = new LinkedList<Attribute>();
    LinkedList<Attribute> groupedAttributes = new LinkedList<Attribute>();
    for (Attribute attribute : relation.type().heading().attributes()){
      if (!toGroup.contains(attribute.name()))
        newAttributes.add(attribute);
      else {
        groupedAttributes.add(attribute);
      }
    }
    if (groupedAttributes.isEmpty())
      throw new RuntimeException("No one the given grouping attributes " + attributes + " are " +
              "in " + relation.type());
    RelationType groupedType = new MemRelationType(new MemHeading(groupedAttributes, 0));
    newAttributes.add(new thebeast.nodmem.type.MemAttribute(as, groupedType));
    return new MemRelationType(new MemHeading(newAttributes, 0));
  }

  protected MemGroup(RelationType type) {
    super(type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitGroup(this);
  }

  public RelationExpression relation() {
    return relation;
  }

  public List<String> attributes() {
    return attributes;
  }

  public String as() {
    return as;
  }
}
