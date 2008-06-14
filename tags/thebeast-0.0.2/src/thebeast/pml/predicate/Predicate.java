package thebeast.pml.predicate;

import thebeast.pml.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:30:37
 */
public abstract class Predicate implements Comparable<Predicate> {

  protected String name;
  protected ArrayList<Type> argumentTypes;


  /**
   * Creates a new predicate with the given name and argument types.
   *
   * @param name          the name of the predicate.
   * @param argumentTypes the list of argument types.
   */
  protected Predicate(String name, List<Type> argumentTypes) {
    this.name = name;
    this.argumentTypes = new ArrayList<Type>(argumentTypes);
  }

  /**
   * Creates a new predicate with the given name and argument types.
   *
   * @param name          the name of the predicate.
   * @param argumentTypes a vararg array of argument types.
   */
  protected Predicate(String name, Type ... argumentTypes) {
    this(name, Arrays.asList(argumentTypes));
  }


  /**
   * Get the name of this predicate.
   *
   * @return this predicate's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the list of argument types for this predicate.
   *
   * @return a list of types corresponding to the types of the arguments of this predicate.
   */
  public List<Type> getArgumentTypes() {
    return argumentTypes;
  }


  public abstract void acceptPredicateVisitor(PredicateVisitor visitor);

  /**
   * Returns a string representation of this predicate.
   *
   * @return the name of this predicate.
   */
  public String toString() {
    StringBuffer result = new StringBuffer(name);
    result.append(": ");
    int index = 0;
    for (Type type :  argumentTypes){
      if (index++>0) result.append(" x ");
      result.append(type);
    }
    return result.toString();
  }

  /**
   * Return the arity of this predicate
   * @return this predicate's arity.
   */
  public int getArity(){
    return argumentTypes.size();
  }


  /**
   * Comparison based on the names of the predicates.
   * @param predicate the predicate to compare to
   * @return a comparison value based on predicate names.
   */
  public int compareTo(Predicate predicate) {
    return name.compareTo(predicate.name);
  }
}
