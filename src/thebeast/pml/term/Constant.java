package thebeast.pml.term;

import thebeast.nod.expression.ScalarExpression;
import thebeast.nod.expression.ExpressionFactory;
import thebeast.pml.TheBeast;
import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:26:52
 */
public abstract class Constant extends Term {

  protected static ExpressionFactory factory = TheBeast.getInstance().getNodServer().expressionFactory();
  
  public Constant(Type type) {
    super(type);
  }

  public abstract ScalarExpression toScalar();

}
