package com.googlecode.thebeast.world;

/**
 * A RelationListener listens to changes made to any relation in a possible world.
 *
 * @author Sebastian Riedel
 */
public interface WorldListener {

    /**
     * Called whenever a tuple was added to a relation of the possible world.
     *
     * @param predicate for which predicate was the tuple added.
     * @param tuple     the tuple which was added.
     */
    void tupleAdded(UserPredicate predicate, Tuple tuple);

}
