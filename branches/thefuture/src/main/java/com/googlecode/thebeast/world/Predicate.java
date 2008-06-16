package com.googlecode.thebeast.world;

import java.util.List;

/**
 * A Predicate objects represents a first order predicate symbol.
 *
 * @author Sebastian Riedel
 */
public interface Predicate extends Symbol {

  /**
   * Returns the name of the predicate symbol.
   *
   * @return a String containing the name of this predicate.
   */
  String getName();

  /**
   * Returns the list of types that denotes which type of constants can be the
   * arguments to this predicate.
   *
   * @return a {@link java.util.RandomAccess} list of types in the order in
   *         which their constants can act as arguments of this predicate.
   */
  List<Type> getArgumentTypes();

}
