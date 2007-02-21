package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.Get;
import thebeast.nod.expression.TupleExpression;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.TupleType;
import thebeast.nod.variable.RelationVariable;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.type.MemTupleType;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 03-Feb-2007 Time: 17:13:12
 */
public class MemGet extends AbstractMemExpression<TupleType> implements Get {

  private RelationVariable relation;
  private TupleExpression backoff;
  private TupleExpression argument;
  private boolean put;


  public MemGet(RelationVariable relation, TupleExpression argument, TupleExpression backoff, boolean put) {
    super(determineType(relation,argument,backoff));
    this.relation = relation;
    this.argument = argument;
    this.backoff = backoff;
    this.put = put;
  }

  protected static TupleType determineType(RelationVariable relation, TupleExpression argument, TupleExpression backoff) {
    HashSet<Attribute> allNames = new HashSet<Attribute>();
    allNames.addAll(relation.type().heading().attributes());
    allNames.removeAll(argument.type().heading().attributes());
    HashSet<Attribute> solutionAttributes = new HashSet<Attribute>();
    solutionAttributes.addAll(backoff.type().heading().attributes());
    MemHeading heading = new MemHeading(new LinkedList<Attribute>(allNames),0);
    if (!solutionAttributes.equals(allNames))
      throw new RuntimeException("For a get expression the backoff type must be the relation type minus the " +
              "attributes from the argument tuple but here we have: " + backoff.type().heading() + " != " +
              heading);
    return new MemTupleType(heading);
  }


  public TupleExpression argument() {
    return argument;
  }

  public RelationVariable relation() {
    return relation;
  }

  public TupleExpression backoff() {
    return backoff;
  }

  public boolean put() {
    return put;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitGet(this);
  }
}
