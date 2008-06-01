package thebeast.nod.expression;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:29:15
 */
public interface Contains extends BoolExpression {

  RelationExpression relation();
  TupleExpression tuple();

}
