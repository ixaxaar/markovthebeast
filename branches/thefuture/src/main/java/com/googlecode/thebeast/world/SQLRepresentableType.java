package com.googlecode.thebeast.world;

import java.util.Iterator;

/**
 * An SQLRepresentableType can represent its constants as values in SQL
 * database. All Type objects in this package are supposed to extend this
 * class.
 *
 * @author Sebastian Riedel
 */
abstract class SQLRepresentableType extends AbstractSymbol implements Type {

  /**
   * Creates an SQLRepresentableType with the given name and signature.
   *
   * @param name      the name of the type.
   * @param signature the signature of the type.
   */
  protected SQLRepresentableType(final String name, final Signature signature) {
    super(name, signature);
  }

  /**
   * Returns the SQL type to represent constants of this type.
   *
   * @return the SQL column type to represent objects of this type.
   */
  abstract String asSQLType();

  /**
   * Get the constant denoted by the given SQL representation.
   *
   * @param representation an SQL representation (as returned by {@link
   *                       java.sql.ResultSet#getObject(int)}).
   * @return the constant denoted by the given representation.
   */
  abstract Constant getConstantFromSQL(Object representation);

  /**
   * Checks whether this type contains the given constant by testing whether the
   * constant's type is this type.
   *
   * @param constant the constant to check whether it is in the type.
   * @return true iff the constant is a member of this type.
   * @see Type#contains(Constant)
   */
  public boolean contains(Constant constant) {
    return constant.getType().equals(this);
  }

  /**
   * By default types are not iterable.
   *
   * @return false by default.
   * @see Type#isIterable()
   */
  public boolean isIterable() {
    return false;
  }

  /**
   * Throws an exception by default.
   *
   * @return nothing since an exception is thrown before.
   *
   * @see Type#iterator()
   */
  public Iterator<Constant> iterator() {
    throw new TypeNotIterableException(this);
  }
}
