package com.googlecode.thebeast.world;

/**
 * @author Sebastian Riedel
 */

/**
 * Exception thrown when a predicate is requested by name and there exist no
 * such predicate.
 */
public final class PredicateNotInSignatureException
  extends SignatureException {

  /**
   * The name of the requested predicate.
   */
  private final String predicateName;

  /**
   * Creates Exception for given type name.
   *
   * @param predicateName the name of the type that was requested.
   * @param signature     the signature that threw this exception.
   */
  PredicateNotInSignatureException(final String predicateName,
                                   final Signature signature) {
    super("There is no predicate with name " + predicateName
      + " in this signature", signature);
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
