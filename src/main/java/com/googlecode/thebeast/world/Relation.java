package com.googlecode.thebeast.world;

import java.util.List;
import java.util.Collection;

/**
 * A Relation object represents a relation over constants. It contains {@link
 * Tuple} objects. The only way to get a Relation object is via the {@link
 * World#getRelation(UserPredicate)} method.
 *
 * @author Sebastian Riedel
 */
public interface Relation extends Collection<Tuple> {

  /**
   * Adds a listener to this relation that will be informed whenever a new tuple
   * was added.
   *
   * @param listener the listener to add.
   */
  void addListener(RelationListener listener);

  /**
   * Removes a listener and stops sending events to it.
   *
   * @param listener the listener to remove.
   */
  void removeListener(RelationListener listener);

  /**
   * Checks whether the tuple is contained in this relation.
   *
   * @param tuple the tuple for which we test whether it is contained in this
   *              relation.
   * @return true iff the relation contains the tuple.
   */
  boolean contains(Tuple tuple);

  /**
   * Returns the argument types of the tuples of this relation.
   *
   * @return the argument types.
   */
  List<? extends Type> getTypes();
}
