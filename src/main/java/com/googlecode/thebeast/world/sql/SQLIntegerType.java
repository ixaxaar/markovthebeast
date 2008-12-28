package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.Constant;
import com.googlecode.thebeast.world.ConstantNameNotInTypeException;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.IntegerConstant;

/**
 * @author Sebastian Riedel
 */
public class SQLIntegerType extends SQLRepresentableType
  implements IntegerType {


  /**
   * Creates an SQLIntegerType.
   *
   * @param name      the name of the type.
   * @param signature the signature of the type.
   */
  SQLIntegerType(final String name, final SQLSignature signature) {
    super(name, signature);
  }

  /**
   * {@inheritDoc}
   */
  String asSQLType() {
    return "integer";
  }

  /**
   * {@inheritDoc}
   */
  Constant getConstantFromSQL(Object representation) {
    return new SQLIntegerConstant(this, (Integer) representation);
  }


  /**
   * Returns <code>Integer.MAX_VALUE</code> as size of this type. Note that this
   * only half of the actual size of this type, but should serve as rough
   * approximation.
   *
   * @return the size of this type.
   */
  public int size() {
    return Integer.MAX_VALUE;
  }

  /**
   * @param name the name of the constant.
   * @return the integer constant corresponding to the integer value represented
   *         by the given string.
   * @throws ConstantNameNotInTypeException if <code>name</code> is not an
   *                                        string that represents an integer.
   */
  public Constant getConstant(String name)
    throws ConstantNameNotInTypeException {
    try {
      return new SQLIntegerConstant(this, Integer.valueOf(name));
    } catch (NumberFormatException e) {
      throw new ConstantNameNotInTypeException(name, this);
    }
  }

  /**
   * Returns a new SQLIntegerConstant and returns it.
   *
   * @param value the value to represent.
   * @return a new SQLIntegerConstant representing the given value.
   */
  public IntegerConstant getConstant(int value) {
    return new SQLIntegerConstant(this, value);
  }
}
