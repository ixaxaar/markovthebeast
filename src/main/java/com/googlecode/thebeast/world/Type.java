package com.googlecode.thebeast.world;

import java.util.Iterator;

/**
 * A type describes a set of constants.
 *
 * @author Sebastian Riedel
 */
public interface Type extends Symbol, Iterable<Constant> {

  /**
   * Checks whether this type contains the given constant.
   *
   * @param constant the constant to check whether it is  in the type.
   * @return true iff the constant is a member of this type.
   */
  boolean contains(Constant constant);

  /**
   * Returns whether it is possible to iterate over the constants of this type.
   * If not calls to the iterator method (and using the type in a foreach loop)
   * will cause an exception.
   *
   * @return if clients can iterate over the constants of this type.
   */
  boolean isIterable();


  /**
   * Returns the number of constants in this type. If this size of the
   * collection is larger than the largest integer ({@link Integer#MAX_VALUE})
   * then the largest integer is to return.
   *
   * @return the number of constants in this type or {@link Integer#MAX_VALUE}
   *         if the type is larger than the largest integer.
   */
  int size();

  /**
   * Method iterator returns an iterator over the constants of this type.
   *
   * @return Iterator<Constant> an iterator over constants in this type.
   * @throws TypeNotIterableException when the type is not iterable.
   * @see Type#isIterable()
   */
  Iterator<Constant> iterator() throws TypeNotIterableException;
}
