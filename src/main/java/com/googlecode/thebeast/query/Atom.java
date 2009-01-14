package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An Atom object represents a First Order Logic Atom: a predicate symbol with a sequence of argument terms.
 *
 * @author Sebastian Riedel
 */
public final class Atom {

    /**
     * The predicate of this atom.
     */
    private final Predicate predicate;

    /**
     * The argument terms of this atom.
     */
    private final List<Term> arguments;

    /**
     * Constructor Atom creates a new atom with the given predicate and arguments.
     *
     * @param predicate the predicate of this atom.
     * @param arguments the arguments of this atom.
     * @throws TypeMismatchException if the argument terms do not match the argument types of the predicate.
     */
    Atom(final Predicate predicate, final List<Term> arguments) throws TypeMismatchException {
        this.predicate = predicate;
        for (int argIndex = 0; argIndex < predicate.getArgumentTypes().size(); ++argIndex) {
            Type expectedType = predicate.getArgumentTypes().get(argIndex);
            Type actualType = arguments.get(argIndex).getType();
            if (!actualType.equals(expectedType))
                throw new TypeMismatchException(expectedType, actualType, arguments.get(argIndex),predicate);
        }
        this.arguments =
            Collections.unmodifiableList(new ArrayList<Term>(arguments));
    }

    /**
     * Method getPredicate returns the predicate of this atom.
     *
     * @return the predicate (type UserPredicate) of this Atom object.
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /**
     * Returns the argument terms of this atom.
     *
     * @return a list of argument terms in proper order.
     */
    public List<Term> getArguments() {
        return arguments;
    }
}
