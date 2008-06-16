package com.googlecode.thebeast.world;

/**
 * A part of the {@link com.googlecode.thebeast.world.Signature} is a Symbol. It
 * has a name maintains a pointer to the signature that contains it.
 *
 * <p>Every symbol in a signature has a unique name that identifies it and can
 * be used to retrieve the symbol using {@link Signature#getSymbol(String)}.
 *
 * <p>Note that the following always holds:
 * <p><code>symbol.getSignature().getSymbol(symbol.getName()).equals(symbol)
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
