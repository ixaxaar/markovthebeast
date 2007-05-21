package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.RelationSelector;
import thebeast.nod.expression.TupleExpression;
import thebeast.nod.type.Heading;
import thebeast.nod.type.RelationType;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemRelationType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemRelationSelector extends AbstractMemExpression<RelationType> implements RelationSelector {

  private ArrayList<TupleExpression> tuples;

  private boolean unify;

  public MemRelationSelector(Heading heading, List<TupleExpression> tuples, boolean unify) {
    super(new MemRelationType((MemHeading) (heading == null ? tuples.get(0).type().heading() : heading)));
    this.tuples = new ArrayList<TupleExpression>(tuples);
    this.unify=unify;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitRelationSelector(this);
  }

  public Heading heading() {
    return type.heading();
  }

  public List<TupleExpression> tupleExpressions() {
    return tuples;
  }

  public boolean unify() {
    return unify;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemRelationSelector that = (MemRelationSelector) o;

    return tuples.equals(that.tuples);

  }

  public int hashCode() {
    return tuples.hashCode();
  }
}
