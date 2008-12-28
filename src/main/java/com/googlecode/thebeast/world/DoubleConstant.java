package com.googlecode.thebeast.world;

/**
 * An DoubleConstant is a Constant representing a double value.
 *
 * @author Sebastian Riedel
 */
public interface DoubleConstant extends Constant {

  /**
   * Returns the double value this constant represents.
   *
   * @return the double value this constant represents.
   */
  double getValue();
}