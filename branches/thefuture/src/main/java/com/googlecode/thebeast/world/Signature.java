package com.googlecode.thebeast.world;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A Signature maintains a set of types, predicates and functions. In
 * particular, it provides a mapping from their String names to the
 * corresponding objects.
 *
 * @author Sebastian Riedel
 */
public interface Signature {
  /**
   * Returns the symbol for the given name.
   *
   * @param name the name of the symbol.
   * @return the symbol with the given name.
   */
  Symbol getSymbol(String name);

  /**
   * Adds the given listener to the listeners of this signature. Will be
   * notified of any changes to it.
   *
   * @param signatureListener a listener to signature events.
   */
  void addSignatureListener(SignatureListener signatureListener);

  /**
   * Removes the specified listener from the list of listeners this signature
   * maintains.
   *
   * @param signatureListener the listener to remove.
   */
  void removeSignatureListener(
    SignatureListener signatureListener);

  /**
   * Creates a new possible world.
   *
   * @return a new possible world with unique id wrt to this signature.
   */
  World createWorld();

  /**
   * Creates a new UserType with the given name.
   *
   * @param name       the name of the type.
   * @param extendable if the type can create new constants when queried for
   *                   constants with unknown names.
   * @return a UserType with the given name.
   * @throws com.googlecode.thebeast.world.SymbolAlreadyExistsException
   *          if there is a symbol with the same name in the signature.
   */
  UserType createType(String name, boolean extendable)
    throws SymbolAlreadyExistsException;

  /**
   * Convenience method to create a type that already contains a set of
   * constants.
   *
   * @param name       the name of the type.
   * @param extendable whether the type should be extendable on the fly.
   * @param constants  a vararg array of constant names.
   * @return a type that contains constants with the provided names.
   */
  UserType createType(String name, boolean extendable,
                      String... constants);

  /**
   * Removes a type from the signature.
   *
   * @param type the type to remove from the signature.
   * @throws com.googlecode.thebeast.world.SymbolNotPartOfSignatureException
   *          if the type is not a member of the signature (e.g. because it was
   *          created by a different signature object).
   */
  void removeType(Type type)
    throws SymbolNotPartOfSignatureException;

  /**
   * Creates a new UserPredicate and stores it in this signature.
   *
   * @param name          the name of the new predicate
   * @param argumentTypes a list with its argument types.
   * @return a UserPredicate with the specified properties.
   * @throws com.googlecode.thebeast.world.SymbolAlreadyExistsException
   *          if there is a symbol with the same name in the signature.
   */
  UserPredicate createPredicate(String name,
                                List<Type> argumentTypes)
    throws SymbolAlreadyExistsException;

  /**
   * Convenience method to create predicates without using a list.
   *
   * @param name          the name of the predicate
   * @param argumentTypes an vararg array of argument types
   * @return a UserPredicate with the specified properties.
   * @throws com.googlecode.thebeast.world.SymbolAlreadyExistsException
   *          if there is a symbol in the signature that already has this name.
   */
  UserPredicate createPredicate(String name,
                                Type... argumentTypes)
    throws SymbolAlreadyExistsException;

  /**
   * Removes a predicate from the signature.
   *
   * @param predicate the predicate to remove from the signature.
   * @throws com.googlecode.thebeast.world.SymbolNotPartOfSignatureException
   *          if the predicate is not a member of the signature (e.g. because it
   *          was created by a different signature object).
   */
  void removePredicate(UserPredicate predicate)
    throws SymbolNotPartOfSignatureException;

  /**
   * Returns the type corresponding to the given type name. An exception is
   * thrown if there is no such type. If you want to find out whether a type
   * exists use {@link com.googlecode.thebeast.world.Signature#getTypeNames()}
   * and {@link java.util.Set#contains(Object)} instead.
   *
   * @param name the name of the type to return.
   * @return either a built-in type of a {@link com.googlecode.thebeast.world.sql.SQLUserType}
   * @throws com.googlecode.thebeast.world.TypeNotInSignatureException
   *          if there is no type with the given name.
   */
  Type getType(String name) throws TypeNotInSignatureException;

  /**
   * Returns the user type corresponding to the given name.
   *
   * @param name the name of the type to return.
   * @return the user type with the given name.
   * @throws com.googlecode.thebeast.world.TypeNotInSignatureException
   *          if there is no type with this name.
   */
  UserType getUserType(String name)
    throws TypeNotInSignatureException;

  /**
   * Returns the set of type names this signature maintains.
   *
   * @return an unmodifiable view on the set of type names.
   */
  Set<String> getTypeNames();

  /**
   * Returns the predicate with the given name if available. Returns both user
   * and built-in predicates.
   *
   * @param name the name of the predicate to return.
   * @return a predicate of this signature with the given name.
   * @throws com.googlecode.thebeast.world.PredicateNotInSignatureException
   *          if there is not predicate with the given name.
   */
  Predicate getPredicate(String name)
    throws PredicateNotInSignatureException;

  /**
   * Returns the set of predicate names this signature maintains.
   *
   * @return an unmodifiable view on the set of predicate names.
   */
  Set<String> getPredicateNames();

  /**
   * Returns the collection user predicates in this signature.
   *
   * @return an unmodifiable view on the set of user predicates.
   */
  Collection<? extends UserPredicate> getUserPredicates();

  /**
   * Returns the set of all types this signature maintains.
   *
   * @return a collection of all types in this signature. Iterating over this
   *         collection maintains the order of type creation.
   */
  Collection<Type> getTypes();
}
