package com.googlecode.thebeast.world;

/**
 * @author Sebastian Riedel
 */

/**
 * Exception thrown when a predicate is requested by name and there exist no such
 * predicate.
 */
public final class PredicateNotInSignatureException
  extends RuntimeException {

  /**
   * The name of the requested predicate.
   */
  private final String predicateName;

  /**
   * Creates Exception for given type name.
   *
   * @param predicateName the name of the type that was requested.
   */
  PredicateNotInSignatureException(final String predicateName) {
    super("There is no predicate with name " + predicateName + " in this signature");
    this.predicateName = predicateName;
  }

  /**
   * The name of the predicate the client wanted to get.
   *
   * @return a string containing the name of the nonexistent predicate.
   */
  public String getPredicateName() {
    return predicateName;
  }

}