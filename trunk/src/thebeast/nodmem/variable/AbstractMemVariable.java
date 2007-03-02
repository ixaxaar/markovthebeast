package thebeast.nodmem.variable;

import thebeast.nod.NoDServer;
import thebeast.nod.type.Type;
import thebeast.nod.value.Value;
import thebeast.nod.variable.Variable;
import thebeast.nodmem.expression.AbstractMemExpression;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractMemVariable<V extends Value, T extends Type>
        extends AbstractMemExpression<T>
        implements Variable<V, T> {

  protected V value;
  protected T type;
  protected MemChunk chunk;
  protected MemVector pointer = new MemVector();
  protected String name;
  protected ArrayList<AbstractMemVariable> owners = new ArrayList<AbstractMemVariable>();
  protected NoDServer server;

  protected static int count = 0;

  protected AbstractMemVariable(NoDServer server, V value, T type) {
    super(type);
    this.value = value;
    this.type = type;
    this.server = server;
    name = "var_" + count++;
  }

  protected AbstractMemVariable(NoDServer server, T type, MemChunk chunk) {
    super(type);
    this.type = type;
    this.chunk = chunk;
    name = "var_" + count++;
    this.server = server;
  }

  public V value() {
    return value;
  }

  public T type(){
    return type;
  }

  public abstract void destroy();



  public String label() {
    return name;
  }

  public MemChunk getContainerChunk() {
    return chunk;
  }

  public MemVector getPointer() {
    return pointer;
  }

  public void setLabel(String label){
    name = label;
  }


  public void own() {
    
  }

  public abstract void copy(AbstractMemVariable other);

  public void invalidate(){
    
  }


}
