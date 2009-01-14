package com.googlecode.thebeast.world;

/**
 * A type that describes the set of double values representable through the Java <code>double</code> primitive type.
 *
 * @author Sebastian Riedel
 */
public interface DoubleType extends Type {


    /**
     * Returns the constant that represents the given value.
     *
     * @param value the value to represent.
     * @return the constant that represents this value.
     */
    DoubleConstant getConstant(double value);

}