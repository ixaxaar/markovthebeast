package com.googlecode.thebeast.world;

/**
 * A type that describes the set of integers representable through the Java
 * <code>int</code> primitive type.
 *
 * @author Sebastian Riedel
 */
public interface IntegerType extends Type {


  /**
   * Returns the constant that represents the given value.
   *
   * @param value the value to represent.
   * @return the constant that represents this value.
   */
  IntegerConstant getConstant(int value);

}
