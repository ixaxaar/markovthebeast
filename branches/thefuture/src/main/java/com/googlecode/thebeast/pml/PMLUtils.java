package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.Substitution;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class PMLUtils {

    /**
     * Returns all nested substitutions that can ground the given clause.
     *
     * @param formula the formula to be grounded with these substitutions.
     * @return a list of all bested substitutions for the given clause.
     */
    public static List<NestedSubstitution> getAllSubstitutions(final PMLFormula formula) {
        return null;
    }

    /**
     * Returns all substitutions for the variables in given list of variables.
     *
     * @param variables the variables to get substitutions for.
     * @return a list of all substitutions for the given variables.
     */
    public static List<Substitution> getAllSubstitutions(final List<Variable> variables) {
        if (variables.size() == 0)
            return Collections.singletonList(new Substitution());
        else {
            List<Substitution> previous = getAllSubstitutions(variables.subList(1, variables.size()));
            Variable currentVariable = variables.get(0);
            if (!currentVariable.getType().isIterable())
                throw new IllegalArgumentException("The type of variable " + currentVariable + " is " +
                    "not iterable and hence we cannot create all substitutions");
            List<Substitution> result = new ArrayList<Substitution>();
            for (Substitution previousSubstitution : previous) {
                for (Constant constant : currentVariable.getType()) {
                    Substitution substitution = new Substitution();
                    substitution.merge(previousSubstitution);
                    substitution.put(currentVariable, constant);
                    result.add(substitution);
                }
            }
            return result;
        }

    }

}
