package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.AbstractSymbol;
import com.googlecode.thebeast.world.Constant;

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
                                     final SQLSignature signature) {
    super(name, signature);
  }

  /**
   * Return an SQL representation of this constant.
   *
   * @return an Object that can be used with {@link
   * java.sql.ResultSet#updateObject(int,Object)}
   * @see SQLRepresentableType
   */
  abstract Object asSQLConstant();

  /**
   * Returns true because a constant is ground.
   * @return true because a constant is ground.
   *
   * @see com.googlecode.thebeast.clause.Term#isGround()
   */
  public boolean isGround() {
    return true;
  }
}
