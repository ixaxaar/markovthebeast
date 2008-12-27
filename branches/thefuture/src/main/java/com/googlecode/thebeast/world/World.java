package com.googlecode.thebeast.world;

import com.googlecode.thebeast.query.Query;
import com.googlecode.thebeast.query.NestedSubstitutionSet;

/**
 * <p>A World represents a possible world or Herbrand Model. It contains a set
 * of ground atoms, either added manually, through solving, or with built-in
 * predicates.</p>
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
   * Returns all groundings of the given clause for which, in this world, the
   * body is true and which contain as existential substitutions all groundings
   * that make the head true wrt to the universal substitution.
   *
   * @param clause the clause to find groundings for.
   * @return a set of groundings for the given clause.
   */
  NestedSubstitutionSet query(Query clause);


}
