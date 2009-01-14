package com.googlecode.thebeast.world;

/**
 * An IntegerConstant is a Constant representing an integer value.
 *
 * @author Sebastian Riedel
 */
public interface IntegerConstant extends Constant {

    /**
     * Returns the integer value this constant represents.
     *
     * @return the integer value this constant represents.
     */
    int getValue();
}
