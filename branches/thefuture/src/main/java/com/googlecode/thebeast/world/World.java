package com.googlecode.thebeast.world;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;

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
public final class World implements RelationListener {

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
   */
  public void tupleAdded(Relation relation, ConstantTuple tuple) {
    for (WorldListener listener : listeners) {
      listener.tupleAdded(relations.inverse().get(relation), tuple);
    }

  }

}
