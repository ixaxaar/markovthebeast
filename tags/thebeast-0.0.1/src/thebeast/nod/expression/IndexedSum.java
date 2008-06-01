package thebeast.nod.expression;

/**
 * @author Sebastian Riedel
 */
public interface IndexedSum extends DoubleExpression {
  ArrayExpression array();
  RelationExpression indexRelation();
  String indexAttribute();
  String scaleAttribute();
}
