package thebeast.nod.expression;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface Summarize extends RelationExpression {

  enum Spec {INT_COUNT,INT_SUM, DOUBLE_COUNT, DOUBLE_SUM}

  RelationExpression relation();
  List<String> by();
  List<ScalarExpression> add();
  List<Spec> specs();
  List<String> as();

}
