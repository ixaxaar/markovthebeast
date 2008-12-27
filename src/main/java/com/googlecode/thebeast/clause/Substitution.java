package com.googlecode.thebeast.clause;

import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.Symbol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Substitution can be used to replace the variables in a formula with terms.
 * It represents a mapping from variables to terms.
 *
 * @author Sebastian Riedel
 */
public final class Substitution {

  /**
   * The mapping from variables to (ground) terms.
   */
  private LinkedHashMap<Variable, Term>
    mapping = new LinkedHashMap<Variable, Term>();

  /**
   * Creates a new substitution with the given mapping.
   *
   * @param mapping from variables to terms.
   */
  public Substitution(final Map<Variable, Term> mapping) {
    this.mapping.putAll(mapping);
  }

  /**
   * Creates an empty substitution.
   */
  public Substitution() {
  }

  /**
   * Maps the given variable to the given term in this substitution.
   *
   * @param variable the variable to be mapped.
   * @param term     the term to map to.
   */
  public void put(final Variable variable, final Term term) {
    mapping.put(variable, term);
  }

  /**
   * Get the term the given variable is mapped to, if existent. If the variable
   * is not mapped to any type null is returned.
   *
   * @param variable the variable that is to be substituted.
   * @return the term the variable is to be substituted with.
   */
  public Term get(final Variable variable) {
    return mapping.get(variable);
  }

  /**
   * Two substitutions are equal if they map the same variables to the same
   * terms.
   *
   * @param o another Substitution.
   * @return true if this and the other substitution map the same variables to
   *         the same terms.
   */
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Substitution that = (Substitution) o;

    if (mapping != null ? !mapping.equals(that.mapping) : that.mapping != null)
      return false;

    return true;
  }

  /**
   * Returns the hashcode of the underlying mapping.
   *
   * @return a hashcode based on the underlying map implementation.
   */
  public int hashCode() {
    return (mapping != null ? mapping.hashCode() : 0);
  }


  /**
   * Prints out a string representation of this substitution.
   *
   * @return a string representation of this substitution.
   */
  public String toString() {
    StringBuffer result = new StringBuffer();
    int index = 0;
    for (Variable var : mapping.keySet()) {
      if (index++ > 0) {
        result.append(", ");
      }
      result.append(var.getName()).append("/").append(mapping.get(var));
    }
    return result.toString();
  }

  /**
   * Creates new substitution using a text format description of the
   * substitution. For example, the string <code>x/Anna, y/Peter</code> will
   * create a substition from the variable "x" to a constant "Anna" in the
   * signature, and "y" will be substituted with a constant "Peter" from the
   * signature.
   *
   * @param signature the signature to get the terms from.
   * @param text      the text format description of a substitution.
   * @return the substitution described by the given text.
   */
  public static Substitution createSubstitution(final Signature signature,
                                                final String text) {
    Substitution substitution = new Substitution();
    String[] split = text.split("[ \t]");
    for (String mapping : split) {
      String[] fromTo = mapping.split("[/]");
      Symbol symbol = signature.getSymbol(fromTo[1]);
      if (symbol instanceof Term) {
        Term term = (Term) symbol;
        Variable var = new Variable(fromTo[0], term.getType());
        substitution.put(var, term);
      } else {
        throw new IllegalArgumentException("Substitution must map to terms" +
          " of the signagure");
      }
    }
    return substitution;
  }

  /**
   * Returns the number of variables in this substitution.
   *
   * @return the number of variables of this substitution.
   */
  public int size() {
    return mapping.size();
  }
}
