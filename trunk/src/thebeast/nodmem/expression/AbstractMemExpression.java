package thebeast.nodmem.expression;

import thebeast.nod.expression.Expression;
import thebeast.nod.type.Type;
import thebeast.nod.util.ExpressionPrinter;
import thebeast.nodmem.mem.MemFunction;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.variable.MemHashIndex;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractMemExpression<T extends Type> implements Expression<T> {

  protected T type;
  protected MemFunction function;
  protected static final MemExpressionCompiler compiler = new MemExpressionCompiler();
  private LinkedList<MemHashIndex> dependendIndices = new LinkedList<MemHashIndex>();

  protected AbstractMemExpression(T type) {
    this.type = type;
  }

  public T type() {
    return type;
  }

  public void needsUpdating() {
    function = null;
  }

  public List<MemHashIndex> dependendIndices() {
    return dependendIndices;
  }

  public void addDependendIndex(MemHashIndex index){
    dependendIndices.add(index);
  }

  
  public String toString() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ExpressionPrinter printer = new ExpressionPrinter(new PrintStream(os));
    acceptExpressionVisitor(printer);
    return new String(os.toByteArray());
  }

  public MemFunction compile() {
    if (function == null) {
      dependendIndices.clear();
      function = compiler.compile(this);
    }
//    else
//      function.clear();
    for (MemHashIndex depIndex : dependendIndices)
      depIndex.update();
    return function;
  }

  public MemFunction compile(MemHeading context) {
    if (function == null) {
      dependendIndices.clear();
      function = compiler.compile(this,context);
    }
    for (MemHashIndex depIndex : dependendIndices)
      depIndex.update();
    return function;
  }

}
