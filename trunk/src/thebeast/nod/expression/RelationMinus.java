package thebeast.nod.expression;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Feb-2007 Time: 16:02:27
 */
public interface RelationMinus extends RelationExpression {

  RelationExpression leftHandSide();
  RelationExpression rightHandSide();

}
