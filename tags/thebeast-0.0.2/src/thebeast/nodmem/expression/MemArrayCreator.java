package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.ArrayCreator;
import thebeast.nod.expression.Expression;
import thebeast.nod.type.ArrayType;
import thebeast.nodmem.type.MemArrayType;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 17:28:15
 */
public class MemArrayCreator extends AbstractMemExpression<ArrayType> implements ArrayCreator {

  private ArrayList<Expression> elements;

  public MemArrayCreator(ArrayType type) {
    super(type);
    elements = new ArrayList<Expression>();
  }

  public MemArrayCreator(List<Expression> elements){
    super(new MemArrayType(elements.get(0).type()));
    this.elements = new ArrayList<Expression>(elements);
  }


  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitArrayCreator(this);
  }

  public List<Expression> elements() {
    return elements;
  }
}
