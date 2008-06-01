package thebeast.nod.expression;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface QueryInsert extends RelationExpression {

  List<String> prefixes();
  List<RelationExpression> relations();
  RelationExpression relation(String prefix);
  BoolExpression where();
  RelationExpression insert();

}
