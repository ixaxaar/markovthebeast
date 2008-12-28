package com.googlecode.thebeast.world;

/**
 * A Symbol object represents one of the following symbols in a First Order
 * Logic signature: a constant, a type or a predicate.
 *
 * <p>Every symbol in a signature has a unique name that identifies it and can
 * be used to retrieve the symbol using {@link Signature#getSymbol(String)}.
 *
 * <p>The following always holds: <p>
 * <code>symbol.getSignature().getSymbol(symbol.getName()).equals(symbol)
 * </code>
 *
 * @author Sebastian Riedel
 */
public interface Symbol {
  /**
   * Returns the name of this symbol. Note that this name is unique over all
   * symbols of a signature.
   *
   * @return a string
   */
  String getName();

  /**
   * Returns the signature this symbol belongs to.
   *
   * @return a Signature that contains this symbol.
   */
  Signature getSignature();

}
