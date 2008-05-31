package thebeast.nod.expression;

import thebeast.nod.type.Type;
import thebeast.nod.variable.RelationVariable;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 03-Feb-2007 Time: 17:09:12
 */
public interface Get extends TupleExpression {
  TupleExpression argument();
  RelationVariable relation();
  TupleExpression backoff();
  boolean put();

}
