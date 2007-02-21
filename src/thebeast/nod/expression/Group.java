package thebeast.nod.expression;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 19:57:56
 */
public interface Group extends RelationExpression {

  RelationExpression relation();
  List<String> attributes();
  String as();

}
