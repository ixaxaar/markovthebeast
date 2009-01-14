package com.googlecode.thebeast.world;

import java.util.AbstractList;
import java.util.List;

/**
 * A Tuple object represents a tuple of constants.
 *
 * @author Sebastian Riedel
 */
public final class Tuple extends AbstractList<Constant> {


    /**
     * Array of constants that backs this tuple.
     */
    private final Constant[] constants;

    /**
     * Creates a tuple with the given constants.
     *
     * @param constants the constants in this tuple.
     */
    public Tuple(final List<Constant> constants) {
        this.constants = constants.toArray(new Constant[constants.size()]);
    }

    /**
     * Creates a tuple with the constants given in the vararg array.
     *
     * @param constants a vararg array of constants.
     */
    public Tuple(final Constant... constants) {
        this.constants = new Constant[constants.length];
        System.arraycopy(constants, 0, this.constants, 0, constants.length);
    }

    /**
     * This method creates a tuple from plain java objects with respect to the argument types of the given predicate.
     *
     * @param predicate the predicate for which the created tuple should be an argument for.
     * @param args      an array of java objects such as Integers, Doubles etc.
     */
    public Tuple(Predicate predicate, Object... args) {
        if (args.length != predicate.getArgumentTypes().size())
            throw new IllegalArgumentException(predicate + " has " + predicate.getArgumentTypes().size()
            + " arguments but you gave us " + args.length);
        this.constants = new Constant[args.length];
        for (int i = 0; i < args.length; ++i) {
            constants[i] =
                predicate.getArgumentTypes().get(i).getConstant(args[i].toString());
        }
    }

    /**
     * Returns the constant at the specified position in this list.
     *
     * @param index index of the constant to return.
     * @return the constant at the specified position in this list.
     * @throws IndexOutOfBoundsException if the given index is out of range (<tt>index &lt; 0 || index &gt;=
     *                                   size()</tt>).
     */
    public Constant get(final int index)
        throws IndexOutOfBoundsException {
        if (index >= constants.length) {
            throw new IndexOutOfBoundsException();
        }
        return constants[index];
    }

    /**
     * Convenience method that returns a UserConstant (if available at the given index).
     *
     * @param index the position of the user constant to return.
     * @return the user constant at the given position.
     */
    public UserConstant getUserConstant(final int index) {
        return (UserConstant) constants[index];
    }


    /**
     * Returns the length of this tuple.
     *
     * @return the number of constants in this tuple.
     */
    public int size() {
        return constants.length;
    }
}
