package com.googlecode.thebeast.clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Grounding can be used to ground a generalized clause.
 *
 * @author Sebastian Riedel
 * @see com.googlecode.thebeast.clause.GeneralizedClause
 */
public final class Grounding {

  /**
   * The universal substitution.
   */
  private final Substitution universalSubstitution;
  /**
   * The existential substitutions.
   */
  private final List<Substitution> existentialSubstitutions;

  /**
   * Creates a new Grounding with given substitutions.
   *
   * @param universalSubstitution    the universal substitution.
   * @param existentialSubstitutions the list of existential substitutions.
   */
  public Grounding(final Substitution universalSubstitution,
                   final List<Substitution> existentialSubstitutions) {
    this.universalSubstitution = universalSubstitution;
    this.existentialSubstitutions = Collections.unmodifiableList(
      new ArrayList<Substitution>(existentialSubstitutions));
  }

  /**
   * Returns the substitution to apply to the universally quantified variables
   * of the generalized clause
   *
   * @return a subtitution that maps each universally quantified variable to a
   *         ground term.
   */
  public Substitution getUniversalSubstitution() {
    return universalSubstitution;
  }

  /**
   * Returns an iterable collection of substitutions for the existential
   * variables of the generalized clause.
   *
   * @return a Collection<Substitution> object that can be used to iterate over
   *         a set of substitutions of the existentially quantified variables of
   *         the generalized clause.
   */
  public List<Substitution> getExistentialSubstitutions() {
    return existentialSubstitutions;
  }

  /**
   * Returns a string representation of this grounding.
   *
   * @return a string representation of this grounding.
   */
  public String toString() {
    return universalSubstitution.toString();
  }

  /**
   * Two groundings are equal if the universal substitutions are equal and the
   * collections of existential substitutions too.
   *
   * @param o the other grounding.
   * @return true iff both groundings are equal.
   */
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Grounding grounding = (Grounding) o;

    if (existentialSubstitutions != null
      ? !existentialSubstitutions.equals(grounding.existentialSubstitutions)
      : grounding.existentialSubstitutions != null)
      return false;
    if (universalSubstitution != null
      ? !universalSubstitution.equals(grounding.universalSubstitution)
      : grounding.universalSubstitution != null)
      return false;

    return true;
  }

  /**
   * Returns a hashcode based on the universal substitution and the collection
   * of existential substitutions.
   *
   * @return a integer hashcode.
   */
  public int hashCode() {
    int result;
    result = (universalSubstitution != null
      ? universalSubstitution.hashCode() : 0);
    result = 31 * result + (existentialSubstitutions != null
      ? existentialSubstitutions.hashCode() : 0);
    return result;
  }
}
