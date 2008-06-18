package com.googlecode.thebeast.world;

/**
 * This exception is thrown whenever clients try to create a symbol with a name
 * for which the signature already has a symbol.
 *
 * @author Sebastian Riedel
 */
public final class SymbolAlreadyExistsException extends SignatureException {

  /**
   * The already existing symbol.
   */
  private final Symbol symbol;

  /**
   * Create a new Signature exception with the corresponding message belonging
   * to the given signature.
   *
   * @param symbol    the already existing symbol with the same name.
   * @param signature the signature that created and threw this exception.
   */
  protected SymbolAlreadyExistsException(final Symbol symbol,
                                         final Signature signature) {
    super("There is already a symbol with the name " + symbol.getName(),
      signature);
    this.symbol = symbol;
  }

  /**
   * The already existing symbol.
   *
   * @return a symbol of the signature that has the requested name.
   */
  public Symbol getSymbol() {
    return symbol;
  }
}
