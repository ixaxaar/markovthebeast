package com.googlecode.thebeast.world;

import com.googlecode.thebeast.query.NestedSubstitutionSet;
import com.googlecode.thebeast.query.Query;

/**
 * <p>A World represents a possible world or Herbrand Model. It contains a set
 * of ground atoms, either added manually, through solving, or with static
 * (built-in) predicates.</p>
 * <p/>
 * By default a World is closed; that is, every ground atom not in the world is
 * considered to be false. However, clients can explicitely set predicates to be
 * open---in this case the world makes no assumptions at all about the extension
 * of the given predicate (it also neglects all ground atoms for the predicate
 * the world might contain until now).
 * <p/>
 * Note that a World represents a view on the relations associated to all (User)
 * predicates in a signature; however, not all of these relations need to be
 * stored in the World itself---some can be provided by "parent" worlds (added
 * by the {@link #addParent(UserPredicate, World)} method). This allows clients
 * to re-use data from one world in another without the need for copying data.
 *
 * @author Sebastian Riedel
 */
public interface World {

  /**
   * Adds a parent world to this world which provides the relation for the given
   * predicate.
   *
   * @param predicate the predicate for which data should be provided by the
   *                  parent.
   * @param parent    the world which provides the data for the specified
   *                  predicate.
   * @throws com.googlecode.thebeast.world.PredicateAlreadyInUseException
   *          if there is already a local relation object for the predicate
   *          (i.e. somebody might have already changed the relation of the
   *          given predicate).
   */
  void addParent(UserPredicate predicate, World parent)
    throws PredicateAlreadyInUseException;

  /**
   * Adds a listener for this world.
   *
   * @param listener a listener that will be informed of changes to the
   *                 relations in this world.
   */
  void addListener(WorldListener listener);

  /**
   * Removes a listener. After this call it will not receice any events
   * concerning this world anymore.
   *
   * @param listener the listener to remove.
   */
  void removeListener(WorldListener listener);

  /**
   * Returns id of this world.
   *
   * @return a unique integer identifying this world among all other worlds
   *         belonging to the same signature.
   */
  int getId();

  /**
   * Return the signature of this world.
   *
   * @return the signature this world belongs to.
   */
  Signature getSignature();

  /**
   * Returns the relation for the given predicate in this possible world. Note
   * that calling this method twice or more times for the same predicate it will
   * return the same Relation object. Thus there only ever exists one Relation
   * object per world and predicate.
   *
   * @param predicate the predicate for which to return the relation.
   * @return the relation the given predicate is associated with via this
   *         possible world.
   */
  Relation getRelation(UserPredicate predicate);


  /**
   * Returns the mutable extension (relation) of the given predicate in this
   * world.
   *
   * @param predicate the predicate for which to return the updatable relation.
   * @return the mutable relation of the given predicate.
   * @throws RelationNotUpdatableException if the relations for the given
   *                                       predicate are not updatable in this
   *                                       world.
   */
  MutableRelation getMutableRelation(UserPredicate predicate)
    throws RelationNotUpdatableException;


  /**
   * Returns all substitutions that satisfy the given query in this world.
   *
   * @param query the query to use.
   * @return a set of substitutions that satisfy the given query.
   */
  NestedSubstitutionSet query(Query query);

  /**
   * Returns true iff the world is open for the given predicate.
   *
   * @param predicate the predicate to check whether the world is open.
   * @return true off the world is open wrt to the given predicate.
   */
  boolean isOpen(UserPredicate predicate);

  /**
   * Sets the world to be open or closed for the given predicate.
   *
   * @param predicate the predicate for which the world should be open/closed.
   * @param open      true if the world should be open for the given predicate,
   *                  false if the world should be closed for the given
   *                  predicate.
   */
  void setOpen(UserPredicate predicate, boolean open);


}
