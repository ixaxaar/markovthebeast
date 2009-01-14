package com.googlecode.thebeast.world;

/**
 * A StaticPredicate represents a predicate for which the interpretation is fixed and already given. That is, if the
 * predicate <code>pred</code> is a StaticPredicate than for any sequence of arguments <code>arg1,arg2,... </code> the
 * truth value of <code>pred(arg1,arg2,...)</code> in every possible world is the same and known in advance (i.e. built
 * into thebeast).
 * <p/>
 * <p>Examples are the equality relation on constant symbols and natural orderings between numbers etc.
 *
 * @author Sebastian Riedel
 */
public interface StaticPredicate extends Predicate {

    /**
     * This method has to return true if the relation of this built-in predicate contains the specified argument tuple.
     *
     * @param arguments the arguments <code>arg1,arg2,... </code> in a list.
     * @return the truth value of <code>pred(arg1,arg2,...)</code> in every possible world.
     */
    boolean evaluate(final Tuple arguments);

}
