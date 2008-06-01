package thebeast.nodmem.expression;

import thebeast.nod.expression.Expression;
import thebeast.nod.expression.Operator;
import thebeast.nod.expression.OperatorInvocation;
import thebeast.nod.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public abstract class MemOperatorInvocation<T extends Type> extends AbstractMemExpression<T>
        implements OperatorInvocation<T> {

  private Operator<T> operator;
  private ArrayList<Expression> args;

  protected MemOperatorInvocation(T type, Operator<T> operator, List<Expression> args){
    super(type);
    this.operator = operator;
    this.args = new ArrayList<Expression>(args);
  }


  public Operator<T> operator() {
    return operator;
  }

  public List<Expression> args() {
    return args; 
  }

 


}
