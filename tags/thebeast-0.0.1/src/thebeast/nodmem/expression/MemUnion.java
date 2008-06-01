package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.Union;
import thebeast.nod.type.Type;
import thebeast.nod.type.RelationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 10-Feb-2007 Time: 17:11:29
 */
public class MemUnion extends AbstractMemExpression<RelationType> implements Union {

  private ArrayList<RelationExpression> arguments;

  protected MemUnion(List<RelationExpression> arguments) {
    super(arguments.get(0).type());
    this.arguments = new ArrayList<RelationExpression>(arguments);
    RelationType type = null;
    for (RelationExpression arg : arguments) {
      if (type == null) type = arg.type();
      else if (!type.equals(arg.type()))
        throw new RuntimeException("Each union member must have the same type, but " + type + " != " + arg.type());
      else
        type = arg.type();
    }
  }


  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitUnion(this);
  }

  public List<RelationExpression> arguments() {
    return arguments;
  }
}
