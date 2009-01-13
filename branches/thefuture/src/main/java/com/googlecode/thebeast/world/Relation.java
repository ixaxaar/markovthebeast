package com.googlecode.thebeast.world;

import java.util.Collection;
import java.util.List;

/**
 * A Relation object represents a relation over constants. It contains {@link
 * Tuple} objects. The only way to get a Relation object is via the {@link
 * World#getRelation(UserPredicate)} method.
 *
 * @author Sebastian Riedel
 */
public interface Relation extends Collection<Tuple> {


  void setOpen(boolean open);


  boolean isOpen();

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

  /**
   * Convenience method that allows clients to add tuples in form of generic
   * vararg arrays.
   *
   * @param args vararg array of arguments, can be constants, Integers, Doubles
   *             or Strings---in the latter cases constants are automatically
   *             generated.
   * @return true (as by contract of the general Collection interface).
   */
  boolean addTuple(Object... args);

  /**
   * Convenience method that allows clients to check whether a relation contains
   * a tuple specified through an argvar array.
   *
   * @param args vararg array of arguments, can be constants, Integers, Doubles
   *             or Strings---in the latter cases constants are automatically
   *             generated.
   * @return if the relation contains the specified tuple.
   */
  boolean containsTuple(Object... args);

}
