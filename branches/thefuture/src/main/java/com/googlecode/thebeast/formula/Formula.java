package com.googlecode.thebeast.formula;

import java.util.List;

/**
 * A Formula object represents a first order logic formula (with some
 * extensions). Formulae have a tree-structure which clients can traverse using
 * {@link Formula#getChildren()}.
 *
 * @author Sebastian Riedel
 */
public interface Formula {

  /**
   * Method getChildren returns the child formulae of this formula. If the
   * formula is a terminal symbol the returned list is empty.
   *
   * @return the children of this formula.
   */
  List<Formula> getChildren();

}
