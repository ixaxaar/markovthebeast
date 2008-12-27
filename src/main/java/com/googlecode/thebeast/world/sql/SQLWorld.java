package com.googlecode.thebeast.world.sql;

import com.google.common.collect.HashBiMap;
import com.googlecode.thebeast.query.GeneralizedClause;
import com.googlecode.thebeast.query.GroundingSet;
import com.googlecode.thebeast.world.PredicateAlreadyInUseException;
import com.googlecode.thebeast.world.Relation;
import com.googlecode.thebeast.world.RelationListener;
import com.googlecode.thebeast.world.RelationNotUpdatableException;
import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.World;
import com.googlecode.thebeast.world.WorldListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>A SQLWorld is a SQL based implementation of a World.
 *
 * @author Sebastian Riedel
 */
final class SQLWorld implements RelationListener, WorldListener, World {

  /**
   * The signature this world belongs to.
   */
  private final SQLSignature signature;

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
  private final HashMap<UserPredicate, SQLWorld>
    parents = new HashMap<UserPredicate, SQLWorld>();

  /**
   * The set of relation objects representing the relations in this possible
   * world.
   */
  private final HashBiMap<UserPredicate, SQLRelation>
    relations = new HashBiMap<UserPredicate, SQLRelation>();

  /**
   * Create a new world belonging to the given signature and with the given id.
   * This will create a set of database tables (and drop old ones with clashing
   * names).
   *
   * @param signature signature this world belongs to.
   * @param id        unique number identifying this world.
   */
  SQLWorld(final SQLSignature signature, final int id) {
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
   *                  predicate. Must be an SQLWorld.
   * @throws com.googlecode.thebeast.world.PredicateAlreadyInUseException
   *          if there is already a local relation object for the predicate
   *          (i.e. somebody already changed the relation here).
   * @see World#addParent(UserPredicate,World)
   */
  public void addParent(final UserPredicate predicate, final World parent)
    throws PredicateAlreadyInUseException {
    SQLWorld sqlParent = (SQLWorld) parent;
    signature.match(predicate.getSignature());
    signature.match(parent.getSignature());
    if (relations.get(predicate) != null) {
      throw new PredicateAlreadyInUseException("Can't add a parent world "
        + " for " + predicate + " because there already exists "
        + "a local relation object for it", predicate, this);
    }
    parents.put(predicate, sqlParent);
    parent.addListener(this);
  }

  /**
   * {@inheritDoc}
   */
  public void addListener(final WorldListener listener) {
    listeners.add(listener);
  }

  /**
   * {@inheritDoc}
   */
  public void removeListener(final WorldListener listener) {
    listeners.remove(listener);
  }

  /**
   * Returns the id of this world. Is passed in the constructor.
   *
   * @see World#getId()
   */
  public int getId() {
    return id;
  }


  /**
   * Return the signature of this world.
   *
   * @return the signature this world belongs to.
   */
  public SQLSignature getSignature() {
    return signature;
  }

  /**
   * Returns the relation for the given predicate in this possible world.
   *
   * @param predicate the predicate for which to return the relation.
   * @return the relation the given predicate is associated with via this
   *         possible world. The relation will be an {@link SQLRelation}
   * @see com.googlecode.thebeast.world.World#getRelation(UserPredicate)
   */
  public Relation getRelation(final UserPredicate predicate) {
    SQLUserPredicate sqlPredicate = (SQLUserPredicate) predicate;
    signature.match(predicate.getSignature());
    World parent = parents.get(predicate);
    if (parent != null) {
      return parent.getRelation(predicate);
    }
    SQLRelation relation = relations.get(predicate);
    if (relation == null) {
      SQLTableDescription tableDescription = signature.getSqlTablePool().
        requestTable(sqlPredicate.getSQLRepresentableArgumentTypes());
      relation = new SQLRelation(tableDescription);
      relations.put(predicate, relation);

    }
    return relation;
  }

  /**
   * Returns the updatable extension (relation) of the given predicate in this
   * world.
   *
   * @param predicate the predicate for which to return the updatable relation.
   * @return the updatable relation of the given predicate.
   * @throws com.googlecode.thebeast.world.RelationNotUpdatableException
   *          if the relations for the given predicate are not updatable in this
   *          world.
   * @see World#getMutableRelation(UserPredicate)
   */
  public SQLRelation getMutableRelation(UserPredicate predicate)
    throws RelationNotUpdatableException {
    return (SQLRelation) getRelation(predicate);
  }

  /**
   * Returns all groundings of the given clause for which, in this world, the
   * body is true and which contain as existential substitutions all groundings
   * that make the head true wrt to the universal substitution.
   *
   * @param clause the clause to find groundings for.
   * @return a set of groundings for the given clause.
   */
  public GroundingSet query(GeneralizedClause clause) {
    return signature.getQueryEngine().query(clause,this);
  }

  /**
   * Method getSQLRelation returns the relation of the corresponding predicate
   * as SQLRelation.
   *
   * @param predicate of type UserPredicate
   * @return SQLRelation the relation of the corresponding predicate.
   */
  SQLRelation getSQLRelation(final SQLUserPredicate predicate) {
    return relations.get(predicate);
  }

  /**
   * releases all database tables this world owns.
   *
   * @throws Throwable if SQL tables couldn't be removed.   \
   */
  protected void finalize() throws Throwable {
    super.finalize();
    for (SQLRelation relation : relations.values()) {
      signature.getSqlTablePool().releaseTable(relation.getTableDescription());
    }
  }

  /**
   * Propagates event to all world listeners.
   *
   * @param relation the relation for which the tuple was added.
   * @param tuple    the added tuple.
   * @see RelationListener#tupleAdded(Relation, Tuple)
   */
  public void tupleAdded(final Relation relation, final Tuple tuple) {
    //this cast avoids a suspicious call warning when
    //using the relation object directory in inverse().get(...)
    SQLRelation sqlRelation = (SQLRelation) relation;
    for (WorldListener listener : listeners) {
      listener.tupleAdded(relations.inverse().get(sqlRelation), tuple);
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
   * @see WorldListener#tupleAdded(UserPredicate, Tuple)
   */
  public void tupleAdded(final UserPredicate predicate,
                         final Tuple tuple) {

    if (parents.containsKey(predicate)) {
      for (WorldListener listener : listeners) {
        listener.tupleAdded(predicate, tuple);
      }
    }
  }
}
