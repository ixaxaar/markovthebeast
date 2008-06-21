package com.googlecode.thebeast.world;

/**
 * A RelationListener listens to changes made to a relation in a possible
 * world.
 *
 * @author Sebastian Riedel
 */
public interface RelationListener {

  /**
   * Called whenever a tuple was added to the relation.
   *
   * @param relation the relation to which the tuple was added.
   * @param tuple    the tuple which as added.
   */
  void tupleAdded(Relation relation, Tuple tuple);

}
