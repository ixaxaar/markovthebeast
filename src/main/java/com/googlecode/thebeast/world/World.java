package com.googlecode.thebeast.world;

import com.googlecode.thebeast.query.NestedSubstitutionSet;
import com.googlecode.thebeast.query.Query;

/**
 * <p>A World represents a possible world or Herbrand Model. It contains a set
 * of ground atoms, either added manually, through solving, or with static
 * (built-in) predicates.</p> <p/> By default a World is closed; that is, every
 * ground atom not in the world is considered to be false. However, clients can
 * explicitely set predicates to be open---in this case the world makes no
 * assumptions at all about the extension of the given predicate (it also
 * neglects all ground atoms for the predicate the world might contain until
 * now). <p/> Note that a World represents a view on the relations associated to
 * all (User) predicates in a signature; however, not all of these relations
 * need to be stored in the World itself---some can be provided by "parent"
 * worlds (added by the {@link #addParent(UserPredicate, World)} method). This
 * allows clients to re-use data from one world in another without the need for
 * copying data.
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
   * This method checks whether this world contains the given ground atom as
   * specified by the predicate and vararg array of objects.
   *
   * @param predicate the predicate of the ground atom.
   * @param args      the arguments of the ground atom, either as {@link
   *                  com.googlecode.thebeast.world.Constant} objects, as
   *                  Doubles, Integers or Strings. In the latter cases the
   *                  objects are implicitely mapped to the corresponding
   *                  Constant objects.
   * @return true iff the world contains the given ground atom.
   */
  boolean containsGroundAtom(UserPredicate predicate, Object... args);

  /**
   * Returns all substitutions that satisfy the given query in this world.
   *
   * @param query the query to use.
   * @return a set of substitutions that satisfy the given query.
   */
  NestedSubstitutionSet query(Query query);

 

}
