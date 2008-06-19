package com.googlecode.thebeast.world;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>A World represents a possible world or Herbrand Model. It contains a set
 * of ground atoms, either added manually, through solving, or with built-in
 * predicates.</p>
 *
 * <p>This implementation is based on using an SQL database as underlying data
 * store.</p>
 *
 * @author Sebastian Riedel
 */
public final class World implements RelationListener, WorldListener {

  /**
   * The signature this world belongs to.
   */
  private final Signature signature;

  /**
   * The id number of this world.
   */
  private final int id;


  /**
   * The listeners of this world.
   */
  private final ArrayList<WorldListener>
    listeners = new ArrayList<WorldListener>();

  /**
   * A mapping from user predicates to parent worlds which provide the relations
   * for the given predicate.
   */
  private final HashMap<UserPredicate, World>
    parents = new HashMap<UserPredicate, World>();

  /**
   * The set of relation objects representing the relations in this possible
   * world.
   */
  private final HashBiMap<UserPredicate, Relation>
    relations = new HashBiMap<UserPredicate, Relation>();

  /**
   * Create a new world belonging to the given signature and with the given id.
   * This will create a set of database tables (and drop old ones with clashing
   * names).
   *
   * @param signature signature this world belongs to.
   * @param id        unique number identifying this world.
   */
  World(final Signature signature, final int id) {
    this.signature = signature;
    this.id = id;
  }


  /**
   * Adds a parent world to this world which provides the relation for the given
   * predicate.
   *
   * @param predicate the predicate for which data should be provided by the
   *                  parent.
   * @param parent    the world which provides the data for the specified
   *                  predicate.
   * @throws PredicateAlreadyInUseException if there is already a local relation
   *                                        object for the predicate (i.e.
   *                                        somebody already changed the
   *                                        relation here).
   */
  public void addParent(final UserPredicate predicate, final World parent)
    throws PredicateAlreadyInUseException {
    signature.match(predicate.getSignature());
    signature.match(parent.getSignature());
    if (relations.get(predicate) != null) {
      throw new PredicateAlreadyInUseException("Can't add a parent world "
        + " for " + predicate + " because there already exists "
        + "a local relation object for it", predicate, this);
    }
    parents.put(predicate, parent);
    parent.addListener(this);
  }

  /**
   * Adds a listener for this world.
   *
   * @param listener a listener that will be informed of changes to the
   *                 relations in this world.
   */
  public void addListener(final WorldListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener. After this call it will not receice any events
   * concerning this world anymore.
   *
   * @param listener the listener to remove.
   */
  public void removeListener(final WorldListener listener) {
    listeners.remove(listener);
  }

  /**
   * Returns id of this world.
   *
   * @return a unique integer identifying this world among all other worlds
   *         belonging to the same signature.
   */
  public int getId() {
    return id;
  }

  /**
   * Return the signature of this world.
   *
   * @return the signature this world belongs to.
   */
  public Signature getSignature() {
    return signature;
  }

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
  public Relation getRelation(final UserPredicate predicate) {
    signature.match(predicate.getSignature());
    World parent = parents.get(predicate);
    if (parent != null) {
      return parent.getRelation(predicate);
    }
    Relation relation = relations.get(predicate);
    if (relation == null) {
      SQLTableDescription tableDescription = signature.getSqlTablePool().
        requestTable(predicate.getSQLRepresentableArgumentTypes());
      relation = new Relation(tableDescription);
      relations.put(predicate, relation);

    }
    return relation;
  }

  /**
   * releases all database tables this world owns.
   *
   * @throws Throwable if SQL tables couldn't be removed.   \
   */
  protected void finalize() throws Throwable {
    super.finalize();
    for (Relation relation : relations.values()) {
      signature.getSqlTablePool().releaseTable(relation.getTableDescription());
    }
  }

  /**
   * Propagates event to all world listeners.
   *
   * @param relation the relation for which the tuple was added.
   * @param tuple    the added tuple.
   *
   * @see RelationListener#tupleAdded(Relation, ConstantTuple)
   */
  public void tupleAdded(final Relation relation, final ConstantTuple tuple) {
    for (WorldListener listener : listeners) {
      listener.tupleAdded(relations.inverse().get(relation), tuple);
    }

  }

  /**
   * Listens to its parent worlds and informs its own listeners of changes in
   * the relevant parts of the parents.
   *
   * <p>Note that a World should only be a listener to its parent worlds.
   *
   * @param predicate the predicate that was changed in a parent. If this world
   *                  uses relation of a parent world for this predicate
   *                  listeners are informed of changes.
   * @param tuple     the tuple that was added to the parent.
   *
   * @see WorldListener#tupleAdded(UserPredicate, ConstantTuple)
   */
  public void tupleAdded(final UserPredicate predicate,
                         final ConstantTuple tuple) {
    if (parents.containsKey(predicate)) {
      for (WorldListener listener : listeners) {
        listener.tupleAdded(predicate, tuple);
      }
    }
  }
}
