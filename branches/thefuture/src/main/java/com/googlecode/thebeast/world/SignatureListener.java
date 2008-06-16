package com.googlecode.thebeast.world;

/**
 * A SignatureListener listens to changes made to the signature. It will be
 * notified whenever new types or predicates are added or removed.
 *
 * @author Sebastian Riedel
 */
public interface SignatureListener {

  /**
   * Called when a new type (both user and built-in) is added to the signature.
   *
   * @param type the type that was added.
   */
  void typeAdded(Type type);

  /**
   * Called when a new predicate (both user and built-in) is added to the
   * signature.
   *
   * @param predicate the predicate that was added.
   */
  void predicateAdded(Predicate predicate);

}
