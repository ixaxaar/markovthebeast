package com.googlecode.thebeast.world;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 * A Signature maintains a set of types, predicates and functions. In
 * particular, it provides a mapping from their String names to the
 * corresponding objects.
 *
 * @author Sebastian Riedel
 * @see Type
 */
public final class Signature implements Serializable {

  /**
   * Serial version id for java serialization.
   */
  private static final long serialVersionUID = 1999L;


  /**
   * A map from type names to types. This map contains user types as well as
   * built-in types.
   *
   * @see UserType
   */
  private final LinkedHashMap<String, Type>
    types = new LinkedHashMap<String, Type>();

  /**
   * A mapping from predicate names to predicates.
   */
  private final LinkedHashMap<String, Predicate>
    predicates = new LinkedHashMap<String, Predicate>();


  /**
   * The list of listeners of this signature.
   */
  private final ArrayList<SignatureListener>
    listeners = new ArrayList<SignatureListener>();

  /**
   * Connection to database that is used to store ground atoms.
   */
  private Connection connection;

  /**
   * Creates a new signature and opens a connection to the H2 database.
   */
  public Signature() {
    try {
      Class.forName("org.h2.Driver");
      connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Provides a signature-wide database connection to classes of this package.
   *
   * @return the database connection of this signature.
   */
  Connection getConnection() {
    return connection;
  }


  /**
   * Adds the given listener to the listeners of this signature. Will be
   * notified of any changes to it.
   *
   * @param signatureListener a listener to signature events.
   */
  public void addSignatureListener(final SignatureListener signatureListener) {
    listeners.add(signatureListener);
  }

  /**
   * Removes the specified listener from the list of listeners this signature
   * maintains.
   *
   * @param signatureListener the listener to remove.
   */
  public void removeSignatureListener(
    final SignatureListener signatureListener) {
    listeners.remove(signatureListener);
  }

  /**
   * Creates a new UserType with the given name.
   *
   * @param name the name of the type.
   * @return a UserType with the given name.
   */
  public UserType createType(final String name) {
    UserType type = new UserType(name, this);
    types.put(name, type);
    for (SignatureListener l : listeners) {
      l.typeAdded(type);
    }
    return type;
  }

  /**
   * Creates a new UserPredicate and stores it in this signature.
   *
   * @param name          the name of the new predicate
   * @param argumentTypes a list with its argument types.
   * @return a UserPredicate with the specified properties.
   */
  public UserPredicate createPredicate(final String name,
                                       final List<Type> argumentTypes) {
    UserPredicate predicate = new UserPredicate(name, argumentTypes, this);
    predicates.put(name, predicate);
    for (SignatureListener l : listeners) {
      l.predicateAdded(predicate);
    }
    return predicate;
  }


  /**
   * Returns the type corresponding to the given type name. An exception is
   * thrown if there is no such type. If you want to find out whether a type
   * exists use {@link Signature#getTypeNames()} and {@link
   * Set#contains(Object)} instead.
   *
   * @param name the name of the type to return.
   * @return either a built-in type of a {@link UserType}
   * @throws TypeNotInSignatureException if there is no type with the given
   *                                     name.
   */
  public Type getType(final String name) throws TypeNotInSignatureException {
    Type type = types.get(name);
    if (type == null) {
      throw new TypeNotInSignatureException(name);
    }
    return type;
  }

  /**
   * Returns the set of type names this signature maintains.
   *
   * @return an unmodifiable view on the set of type names.
   */
  public Set<String> getTypeNames() {
    return Collections.unmodifiableSet(types.keySet());
  }


  /**
   * Returns the predicate with the given name if available. Returns both user
   * and built-in predicates.
   *
   * @param name the name of the predicate to return.
   * @return a predicate of this signature with the given name.
   * @throws PredicateNotInSignatureException
   *          if there is not predicate with the given name.
   */
  public Predicate getPredicate(final String name)
    throws PredicateNotInSignatureException {
    return predicates.get(name);
  }

  /**
   * Returns the set of predicate names this signature maintains.
   *
   * @return an unmodifiable view on the set of predicate names.
   */
  public Set<String> getPredicateNames() {
    return Collections.unmodifiableSet(predicates.keySet());
  }

  /**
   * Returns the set of all types this signature maintains.
   *
   * @return a collection of all types in this signature. Iterating over this
   *         collection maintains the order of type creation.
   */
  public Collection<Type> getTypes() {
    return Collections.unmodifiableCollection(types.values());
  }

}
