package thebeast.nod.expression;

/**
 * @author Sebastian Riedel
 */
public interface SparseAdd extends RelationExpression {
  RelationExpression leftHandSide();
  RelationExpression rightHandSide();
  DoubleExpression scale();

  String indexAttribute();
  String valueAttribute();

}
