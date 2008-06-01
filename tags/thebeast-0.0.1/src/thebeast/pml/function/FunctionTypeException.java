package thebeast.pml.function;

import thebeast.pml.term.Term;
import thebeast.pml.function.Function;

/**
 * @author Sebastian Riedel
 */
public class FunctionTypeException extends RuntimeException {

  private Term observed;
  private Function function;
  private int argumentIndex;


  public FunctionTypeException(Function function, int argumentIndex, Term observed) {
    super("function " + function + " expects a " + function.getArgumentTypes().get(argumentIndex).getName() +
            " term in position " + argumentIndex + " but got a " + observed.getType().getName() +
            " for " + observed);
    this.function = function;
    this.argumentIndex = argumentIndex;
    this.observed = observed;
  }


  public int getArgumentIndex() {
    return argumentIndex;
  }

  public Term getObserved() {
    return observed;
  }


  public Function getFunction() {
    return function;
  }
}