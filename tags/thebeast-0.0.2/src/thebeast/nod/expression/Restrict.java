package thebeast.nod.expression;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Jan-2007 Time: 14:39:01
 */
public interface Restrict extends RelationExpression {

  RelationExpression relation();
  BoolExpression where();


}
