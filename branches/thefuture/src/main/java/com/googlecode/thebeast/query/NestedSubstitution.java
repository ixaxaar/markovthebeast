package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Signature;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A NestedSubstitution is a substitution of variables (the outer substitution) that contains a nested set of
 * substitutions (the inner substitutions). It can represent the result of a Query.
 *
 * @author Sebastian Riedel
 * @see Query
 */
public final class NestedSubstitution {

    /**
     * The universal substitution.
     */
    private final Substitution outerSubstitution;
    /**
     * The existential substitutions.
     */
    private final Set<Substitution> innerSubstitutions;

    /**
     * Creates a new NestedSubstitution with given inner and outer substitutions.
     *
     * @param outerSubstitution  the outer substitution.
     * @param innerSubstitutions the set of inner substitutions.
     */
    public NestedSubstitution(final Substitution outerSubstitution,
                              final Collection<Substitution> innerSubstitutions) {
        this.outerSubstitution = outerSubstitution;
        if (innerSubstitutions.size() == 0) {
            this.innerSubstitutions = Collections.unmodifiableSet(
                Collections.singleton(new Substitution())
            );
        } else {
            this.innerSubstitutions = Collections.unmodifiableSet(
                new HashSet<Substitution>(innerSubstitutions));
        }
    }


    /**
     * Returns true iff the substitution does not contain inner substitutions.
     *
     * @return true iff the substitution does not contain inner substitutions.
     */
    public boolean isNotNested() {
        return innerSubstitutions.size() == 1 &&
            innerSubstitutions.iterator().next().size() == 0;
    }

    /**
     * Returns the substitution to apply to the universally quantified variables of the generalized clause
     *
     * @return a subtitution that maps each universally quantified variable to a ground term.
     */
    public Substitution getOuterSubstitution() {
        return outerSubstitution;
    }

    /**
     * Returns an iterable collection of substitutions for the existential variables of the generalized clause.
     *
     * @return a Collection<Substitution> object that can be used to iterate over a set of substitutions of the
     *         existentially quantified variables of the generalized clause.
     */
    public Set<Substitution> getInnerSubstitutions() {
        return innerSubstitutions;
    }

    /**
     * Returns a string representation of this nested substitution .
     *
     * @return a string representation of this nested substitution .
     */
    public String toString() {
        StringBuffer result = new StringBuffer("{");
        result.append(outerSubstitution.toString());
        if (isNotNested()) {
            result.append("}");
            return result.toString();
        }
        result.append(" ");
        int index = 0;
        result.append("{");
        for (Substitution s : innerSubstitutions) {
            if (index++ > 0) {
                result.append(", ");
            }
            result.append(s.toString());
        }
        result.append("}");
        return result.toString();
    }

    /**
     * Creates a list of nested substitutions. This method assumes strings formatted according to {@link
     * #createNestedSubstitution(com.googlecode.thebeast.world.Signature, String)}.
     *
     * @param signature     the signature to use.
     * @param substitutions the nested substitution strings.
     * @return all nested substitions represented by the strings in <code>substitions</code>
     */
    public static List<NestedSubstitution> createNestedSubstitutions(
        final Signature signature,
        final String... substitutions) {

        ArrayList<NestedSubstitution>
            result = new ArrayList<NestedSubstitution>(substitutions.length);
        for (String text : substitutions)
            result.add(createNestedSubstitution(signature, text));
        return result;
    }

    /**
     * Create a new NestedSubstitution based on a string representation. The required format is
     * <pre>
     *  x1/c1  x2/c2 ...  {y1/e1 y2/e2 ...} {y1/e3 y2/e4 ...} ...
     * </pre>
     * Here every constant <code>c1,c2,...,e1,e2,...</code> must be a ground term of the signature.
     *
     * @param signature the signature to use when resolving term names.
     * @param text      the text that represents the grounding using the format described above.
     * @return the NestedSubstitution described by the provided text.
     */
    public static NestedSubstitution createNestedSubstitution(
        final Signature signature,
        final String text) {

        int startOfExistentials = text.indexOf('{');
        String universalString = startOfExistentials == -1
            ? text
            : text.substring(0, startOfExistentials);
        Substitution universal =
            Substitution.createSubstitution(signature, universalString);
        Pattern innerStrings = Pattern.compile("\\{([^\\}]+)\\}");
        Matcher m = innerStrings.matcher(text);
        Set<Substitution> innerSubstitutions = new HashSet<Substitution>();
        while (m.find()) {
            for (int g = 0; g < m.groupCount(); ++g) {
                innerSubstitutions.add(Substitution.createSubstitution(signature,
                    m.group(g + 1)));
            }
        }
        return new NestedSubstitution(universal, innerSubstitutions);
    }


    /**
     * Two NestedSubstitution are equal if the outer substitutions are equal and the collections of inner substitutions
     * too.
     *
     * @param o the other grounding.
     * @return true iff both groundings are equal.
     */
    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NestedSubstitution nestedSubstitution = (NestedSubstitution) o;

        if (innerSubstitutions != null
            ? !innerSubstitutions.equals(nestedSubstitution.innerSubstitutions)
            : nestedSubstitution.innerSubstitutions != null)
            return false;
        if (outerSubstitution != null
            ? !outerSubstitution.equals(nestedSubstitution.outerSubstitution)
            : nestedSubstitution.outerSubstitution != null)
            return false;

        return true;
    }

    /**
     * Returns a hashcode based on the outer substitution and the collection of inner substitutions.
     *
     * @return a integer hashcode.
     */
    public int hashCode() {
        int result;
        result = (outerSubstitution != null
            ? outerSubstitution.hashCode() : 0);
        result = 31 * result + (innerSubstitutions != null
            ? innerSubstitutions.hashCode() : 0);
        return result;
    }
}
