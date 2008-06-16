package com.googlecode.thebeast.world;

/**
 * An abstract helper implementation of Symbol. Provides a simple implementation
 * of this methods required by the Symbol interface.
 *
 * @author Sebastian Riedel
 */
public class AbstractSymbol implements Symbol {

  /**
   * The name of this symbol.
   */
  private final String name;

  /**
   * The signature of this symbol.
   */
  private final Signature signature;

  /**
   * Creates an AbstractSymbol with the given name and signature.
   *
   * @param name      the name of the symbol.
   * @param signature the signature of the symbol.
   */
  protected AbstractSymbol(final String name, final Signature signature) {
    this.name = name;
    this.signature = signature;
  }

  /**
   * Returns the name of this symbol.
   *
   * @return String with name of this symbol.
   */
  public final String getName() {
    return name;
  }

  /**
   * Returns the signature this symbol belongs to.
   *
   * @return Signature of this symbol.
   */
  public final Signature getSignature() {
    return signature;
  }
}
