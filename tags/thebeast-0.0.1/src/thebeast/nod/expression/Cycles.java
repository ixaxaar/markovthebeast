package thebeast.nod.expression;

/**
 * @author Sebastian Riedel
 */
public interface Cycles extends RelationExpression {

  RelationExpression graph();
  String fromAttribute();
  String toAttribute();

}
