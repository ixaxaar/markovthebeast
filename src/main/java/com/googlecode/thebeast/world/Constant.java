package com.googlecode.thebeast.world;

/**
 * A Constant is a First Order Logic Symbol that represents an object within a
 * domain. In Markov Logic we assume Herbrand Semantics, i.e., the object values
 * the constants represent are the constants themselves.
 *
 * @author Sebastian Riedel
 */
public interface Constant extends Symbol{

  /**
   * Returns the type of this constant.
   *
   * @return the Type object this constant belongs to.
   */
  Type getType();

  /**
   * Each constant has a name that can be used to refer to the constant in any
   * textual context.
   *
   * @return String containing the name of this constant.
   *
   * @see Symbol#getName()
   */
  String getName();
}
