package thebeast.nod.expression;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 17:27:16
 */
public interface ArrayCreator extends ArrayExpression {

  List<Expression> elements();

}
