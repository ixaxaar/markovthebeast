package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Type;

import java.util.*;

/**
 * A Variable object represents a First Order Logic variable.
 *
 * @author Sebastian Riedel
 */
public final class Variable implements Term {

    /**
     * The name of the variable.
     */
    private final String name;

    /**
     * The type of the variable.
     */
    private final Type type;

    /**
     * Creates a new variable with the given name and type.
     *
     * @param name the name of the variable.
     * @param type the type of the variable.
     */
    public Variable(final String name, final Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Method getName returns the name of this variable.
     *
     * @return the name (type String) of this variabe.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the type of this variable.
     *
     * @return the type of this variable.
     * @see com.googlecode.thebeast.query.Term#getType()
     */
    public Type getType() {
        return type;
    }

    /**
     * A variable is, by definition, not ground. Hence this method returns <code>false</code>
     *
     * @return <code>false</code> because a variable is not ground.
     * @see Term#isGround()
     */
    public boolean isGround() {
        return false;
    }

    /**
     * Two variables are equal if they have the same name and type.
     *
     * @param o the other variable.
     * @return true if both variables have the same name and type.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (name != null ? !name.equals(variable.name) : variable.name != null)
            return false;
        if (type != null ? !type.equals(variable.type) : variable.type != null)
            return false;

        return true;
    }

    /**
     * Returns a hashcode based on the hashcode of the name and the hashcode of the type.
     *
     * @return a hashcode based on the hashcodes of the name and type.
     */
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    /**
     * Collects all variables in a collection of atoms.
     *
     * @param atoms the atoms to collect the variables from.
     * @return the set of variables mentioned in the given atoms. The set is ordered in the order the variables appear
     *         in the atoms collection.
     */
    public static List<Variable> getAllVariables(Collection<Atom> atoms) {
        LinkedHashSet<Variable> result = new LinkedHashSet<Variable>();
        for (Atom atom : atoms) {
            for (Term term : atom.getArguments()) {
                if (term instanceof Variable) result.add((Variable) term);
            }
        }
        return new ArrayList<Variable>(result);
    }


    /**
     * Returns a string representation of this variable using type name and variable name.
     *
     * @return string representation of this variable.
     */
    @Override
    public String toString() {
        return type.getName() + ":" + getName();
    }
}
