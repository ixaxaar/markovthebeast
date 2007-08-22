package thebeast.nodmem.expression;

import thebeast.nod.expression.Expression;
import thebeast.nod.type.Type;
import thebeast.nod.util.ExpressionPrinter;
import thebeast.nodmem.mem.MemFunction;
import thebeast.nodmem.type.MemHeading;
import thebeast.nodmem.variable.MemHashIndex;
import thebeast.nodmem.variable.MemRelationVariable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractMemExpression<T extends Type> implements Expression<T> {

  protected T type;
  protected MemFunction function;
  protected static final MemExpressionCompiler compiler = new MemExpressionCompiler();
  private LinkedList<MemHashIndex> dependendIndices = new LinkedList<MemHashIndex>();

  private static LinkedList<WeakReference<Expression>> references = new LinkedList<WeakReference<Expression>>();
  private static ReferenceQueue<Expression> queue = new ReferenceQueue<Expression>();
  protected ArrayList<MemRelationVariable> sideEffected = new ArrayList<MemRelationVariable>();

  protected AbstractMemExpression(T type) {
    this.type = type;
    //references.add(new WeakReference<Expression>(this, queue));
  }

  public static List<WeakReference<Expression>> references(){
    return references;    
  }

  public T type() {
    return type;
  }

  public void needsUpdating() {
    function = null;
    dependendIndices.clear();
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

  public int byteSize(){
    return function != null ? function.bytesize() : 0;
  }

}
