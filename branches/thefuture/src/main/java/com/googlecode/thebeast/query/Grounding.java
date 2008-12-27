package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Signature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Grounding can be used to ground a generalized clause. It consists of a
 * substitution of the unversally quantified variables in the clause and a set
 * of substitutions for the existential variables in the clause.
 * <p/>
 * <p>The set of existential substitutions will at least contain a single
 * substitution because
 * grounding a clause without existential substitutions is undefined. If the
 * clause has no existentially quantified variables then there will exactly one
 * existential substitution which is empty.
 *
 * @author Sebastian Riedel
 * @see com.googlecode.thebeast.query.GeneralizedClause
 */
public final class Grounding {

  /**
   * The universal substitution.
   */
  private final Substitution universalSubstitution;
  /**
   * The existential substitutions.
   */
  private final Set<Substitution> existentialSubstitutions;

  /**
   * Creates a new Grounding with given substitutions. Note that if the set of
   * existential substitutions is empty the grounding will have a single empty
   * existential substitution.
   *
   * @param universalSubstitution    the universal substitution.
   * @param existentialSubstitutions the set of existential substitutions.
   */
  public Grounding(final Substitution universalSubstitution,
                   final Set<Substitution> existentialSubstitutions) {
    this.universalSubstitution = universalSubstitution;
    if (existentialSubstitutions.size() == 0) {
      this.existentialSubstitutions = Collections.unmodifiableSet(
        Collections.singleton(new Substitution())
      );
    } else {
      this.existentialSubstitutions = Collections.unmodifiableSet(
        new HashSet<Substitution>(existentialSubstitutions));
    }
  }


  /**
   * Returns true if this grounding has only one existential substitution and
   * this substitution is empty. In this case it can only be used to ground
   * clauses without existential variables.
   *
   * @return true iff if the grounding has only one existential substitution and
   *         this substitution is empty.
   */
  public boolean isPurelyUniversal() {
    return existentialSubstitutions.size() == 1 &&
      existentialSubstitutions.iterator().next().size() == 0;
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
  public Set<Substitution> getExistentialSubstitutions() {
    return existentialSubstitutions;
  }

  /**
   * Returns a string representation of this grounding.
   *
   * @return a string representation of this grounding.
   */
  public String toString() {
    StringBuffer result = new StringBuffer("{");
    result.append(universalSubstitution.toString());
    if (isPurelyUniversal()) {
      result.append("}");
      return result.toString();
    }
    result.append(" ");
    int index = 0;
    result.append("{");
    for (Substitution s : existentialSubstitutions) {
      if (index++ > 0) {
        result.append(", ");
      }
      result.append("{");
      result.append(s.toString());
      result.append("}");
    }
    result.append("}");
    return result.toString();
  }

  /**
   * Create a new grounding based on a string representation. The required
   * format is
   * <pre>
   *  x1/c1, x2/c2 ...  {y1/e1,y2/e2 ...} {y1/e3,y2/e4 ...} ...
   * </pre>
   * Here every constant <code>c1,c2,...,e1,e2,...</code> must be a ground term
   * of the signature.
   *
   * @param signature the signature to use when resolving term names.
   * @param text      the text that represents the grounding using the format
   *                  described above.
   * @return the grounding described by the provided text.
   */
  public static Grounding createGrounding(final Signature signature,
                                          final String text) {

    int startOfExistentials = text.indexOf('{');
    String universalString = startOfExistentials == -1
      ? text
      : text.substring(0, startOfExistentials);
    Substitution universal =
      Substitution.createSubstitution(signature, universalString);
    Pattern existentialStrings = Pattern.compile("\\{([^\\}]+)\\}");
    Matcher m = existentialStrings.matcher(text);
    Set<Substitution> universals = new HashSet<Substitution>();
    while (m.find()) {
      for (int g = 0; g < m.groupCount(); ++g) {
        universals.add(Substitution.createSubstitution(signature,
          m.group(g + 1)));
      }
    }
    return new Grounding(universal, universals);
  }


  /**
   * Two groundings are equal if the universal substitutions are equal and the
   * collections of existential substitutions too.
   *
   * @param o the other grounding.
   * @return true iff both groundings are equal.
   */
  public boolean equals(final Object o) {
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
