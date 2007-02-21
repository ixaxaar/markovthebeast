package thebeast.pml.term;

import thebeast.pml.term.Term;
import thebeast.pml.term.TermVisitor;
import thebeast.pml.function.Function;
import thebeast.pml.Type;

import java.util.List;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:28:03
 */
public class FunctionApplication extends Term {

  private Function function;
  private List<Term> arguments;

  public FunctionApplication(Function function, List<Term> arguments) {
    super(function.getReturnType());
    this.function = function;
    this.arguments = arguments;
  }

  public FunctionApplication(Function function, Term ... arguments) {
    this(function, Arrays.asList(arguments));
  }

  public List<Term> getArguments() {
    return arguments;
  }

  public Function getFunction() {
    return function;
  }

  public void acceptTermVisitor(TermVisitor visitor) {
    visitor.visitFunctionApplication(this);
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    FunctionApplication that = (FunctionApplication) o;

    if (!arguments.equals(that.arguments)) return false;
    if (!function.equals(that.function)) return false;

    return true;
  }

  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + function.hashCode();
    result = 31 * result + arguments.hashCode();
    return result;
  }

  public boolean isNonPositive() {
    return function.getReturnType().getTypeClass() == Type.Class.NEGATIVE_DOUBLE;
  }

  public boolean isNonNegative() {
    return function.getReturnType().getTypeClass() == Type.Class.POSITIVE_DOUBLE;
  }
}
