package thebeast.pml.function;

import thebeast.pml.Type;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:20:41
 */
public abstract class Function implements Comparable<Function> {

  protected Type returnType;
  protected List<Type> argumentTypes;
  protected String name;

  protected Function(String name, Type returnType, List<Type> argumentTypes) {
    this.name = name;
    this.returnType = returnType;
    this.argumentTypes = new ArrayList<Type>(argumentTypes);
  }

  protected Function(String name, Type returnType, Type ... argumentTypes) {
    this.name = name;
    this.returnType = returnType;
    this.argumentTypes = new ArrayList<Type>(Arrays.asList(argumentTypes));
  }

  public String getName() {
    return name;
  }

  public List<Type> getArgumentTypes() {
    return argumentTypes;
  }

  public Type getReturnType() {
    return returnType;
  }


  public int compareTo(Function function) {
    return name.compareTo(function.getName());
  }

  public abstract void acceptFunctionVisitor(FunctionVisitor visitor);

}
