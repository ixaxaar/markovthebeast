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

  /**
   * The Closedness of a relation defines how the existing and non-existing
   * tuples in the tuple collection contained in a Relation object are
   * interpreted.
   */
  enum Closedness {

    /**
     * If a relation is Open, all tuples contained in the tuple set are given a
     * user defined probability, and all others are given probability 0.5.
     */
    Open,

    /**
     * If a relation is SemiOpen, all tuples contained in the tuple set are
     * given a user defined probability, and all others are given probability
     * 0.0.
     */
    SemiOpen,

    /**
     * If a relation is Closed, all tuples contained in the tuple set are given
     * probability 1.0 (even if they have been given another probability by the
     * client), and all others are given 0.0.
     */
    Closed
  }


  /**
   * Explicitely a Relation contains a set of tuples (with probabilities). How
   * these tuples are interpreted depends on the "closedness" of the relation.
   * This method sets this closedness.
   *
   * @param closedness the closedness of the relation.
   */
  void setClosedness(Closedness closedness);


  /**
   * Explicitely a Relation contains a set of tuples (with probabilities). How
   * these tuples are interpreted depends on the "closedness" of the relation.
   * This method returns this closedness.
   *
   * @return the closedness of the relation.
   */
  Closedness getClosedness();

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
