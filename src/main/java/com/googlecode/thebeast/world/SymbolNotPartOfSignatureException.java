package com.googlecode.thebeast.world;

/**
 * This exception is thrown whenever clients try to remove a symbol from a
 * signature that does not contain it.
 *
 * @author Sebastian Riedel
 */
public final class SymbolNotPartOfSignatureException
  extends SignatureException {

  /**
   * The already existing symbol.
   */
  private final Symbol symbol;

  /**
   * Create a new Signature exception with the corresponding message belonging
   * to the given signature.
   *
   * @param symbol    the symbol which is not part of the signature.
   * @param signature the signature that created and threw this exception.
   */
  protected SymbolNotPartOfSignatureException(final Symbol symbol,
                                              final Signature signature) {
    super("This symbol is not a member of this signature: " + symbol.getName(),
      signature);
    this.symbol = symbol;
  }

  /**
   * The symbol which is not part of this signature.
   *
   * @return a symbol that was requested to be removed but is not part of the
   *         signature.
   */
  public Symbol getSymbol() {
    return symbol;
  }
}
