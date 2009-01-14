package com.googlecode.thebeast.world;

/**
 * A MutableRelation is a Relation that can be changed.
 *
 * @author Sebastian Riedel
 */
public interface MutableRelation extends Relation {

    /**
     * Adds a tuple to the relation.
     *
     * @param tuple the tuple to add.
     */
    boolean add(Tuple tuple);
}
