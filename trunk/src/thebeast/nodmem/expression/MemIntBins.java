package thebeast.nodmem.expression;

import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.IntBins;
import thebeast.nod.expression.IntExpression;
import thebeast.nod.type.IntType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 30-Jan-2007 Time: 20:31:49
 */
public class MemIntBins extends AbstractMemExpression<IntType> implements IntBins {

  private IntExpression argument;
  private List<Integer> bins;

  protected MemIntBins(IntType type, IntExpression argument, List<Integer> bins) {
    super(type);
    this.bins = new ArrayList<Integer>(bins);
    this.argument = argument;
  }


  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitIntBin(this);
  }

  public List<Integer> bins() {
    return bins;
  }

  public IntExpression argument() {
    return argument;
  }
}
