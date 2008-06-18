package com.googlecode.thebeast.world;

/**
 * An SQLRepresentableConstant is a constant which can be represented as a value
 * in an SQL table cell.
 *
 * @author Sebastian Riedel
 */
abstract class SQLRepresentableConstant extends AbstractSymbol
  implements Constant {

  /**
   * Creates an SQLRepresentableConstant with the given name and signature.
   *
   * @param name      the name of the symbol.
   * @param signature the signature of the symbol.
   */
  protected SQLRepresentableConstant(final String name,
                                     final Signature signature) {
    super(name, signature);
  }

  /**
   * Return an SQL representation of this constant.
   *
   * @return an Object that can be used with {@link
   * java.sql.ResultSet#updateObject(int,Object)}
   * @see com.googlecode.thebeast.world.SQLRepresentableType
   */
  abstract Object asSQLConstant();

}
