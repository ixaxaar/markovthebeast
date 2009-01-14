package com.googlecode.thebeast.world;

/**
 * This exception is thrown when a client attempts to assign a parent world for a particular predicate but there is
 * already a relation for the predicate in use. This could cause inconsistencies (when one clients changes the local
 * version and another changes the parent version).
 *
 * @author Sebastian Riedel
 * @see com.googlecode.thebeast.world.sql.SQLWorld
 * @see com.googlecode.thebeast.world.sql.SQLRelation
 */
public final class PredicateAlreadyInUseException extends RuntimeException {

    /**
     * The predicate that is already in use.
     */
    private final UserPredicate predicate;

    /**
     * the world in which the exception occured.
     */
    private final World world;


    /**
     * Creates a new PredicateAlreadyInUseException with the given properties.
     *
     * @param message   the message to display.
     * @param predicate the predicate which is already in use.
     * @param world     the world in which the exception occured.
     */
    public PredicateAlreadyInUseException(final String message,
                                          final UserPredicate predicate,
                                          final World world) {
        super(message);
        this.predicate = predicate;
        this.world = world;
    }

    /**
     * Returns the predicate for there already exists a local relation object.
     *
     * @return the predicate already in use.
     */
    public UserPredicate getPredicate() {
        return predicate;
    }

    /**
     * Returns the world that threw the exception.
     *
     * @return the world for which the exception occured.
     */
    public World getWorld() {
        return world;
    }
}
