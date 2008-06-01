package thebeast.nod.expression;

/**
 * @author Sebastian Riedel
 */
public interface IndexCollector extends RelationExpression{
  RelationExpression grouped();
  String groupAttribute();
  String indexAttribute();
  String valueAttribute();
}
