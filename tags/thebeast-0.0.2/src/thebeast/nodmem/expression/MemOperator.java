package thebeast.nodmem.expression;

import thebeast.nod.type.Type;
import thebeast.nod.expression.Operator;
import thebeast.nod.expression.Expression;
import thebeast.nod.variable.Variable;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class MemOperator<R extends Type> implements Operator<R> {

  private ArrayList<Variable> args;
  private Expression<R> result;
  private String name;


  public MemOperator(String name, Expression<R> result, List<? extends Variable> args) {
    this.result = result;
    this.name = name;
    this.args = new ArrayList<Variable>(args);
  }

  public List<Variable> args() {
    return args;
  }

  public Expression<R> result() {
    return result;
  }

  public String name() {
    return name;
  }
}
