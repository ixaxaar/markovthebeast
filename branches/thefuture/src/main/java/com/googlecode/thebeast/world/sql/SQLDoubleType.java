package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.*;

/**
 * @author Sebastian Riedel
 */
public class SQLDoubleType extends SQLRepresentableType
  implements DoubleType {


  /**
   * Creates an SQLIntegerType.
   *
   * @param name      the name of the type.
   * @param signature the signature of the type.
   */
  SQLDoubleType(final String name, final SQLSignature signature) {
    super(name, signature);
  }

  /**
   * {@inheritDoc}
   */
  String asSQLType() {
    return "double";
  }

  /**
   * {@inheritDoc}
   */
  Constant getConstantFromSQL(Object representation) {
    return new SQLDoubleConstant(this, (Double) representation);
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
   * @return the double constant corresponding to the double value represented
   *         by the given string.
   * @throws com.googlecode.thebeast.world.ConstantNameNotInTypeException
   *          if <code>name</code> is not an string that represents a double.
   */
  public Constant getConstant(String name)
    throws ConstantNameNotInTypeException {
    try {
      return new SQLDoubleConstant(this, Double.valueOf(name));
    } catch (NumberFormatException e) {
      throw new ConstantNameNotInTypeException(name, this);
    }
  }

  public DoubleConstant getConstant(double value) {
    return new SQLDoubleConstant(this, value);
  }
}