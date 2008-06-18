package com.googlecode.thebeast.world;

import java.util.AbstractList;
import java.util.List;

/**
 * A ConstantTuple object represents a tuple of constants.
 *
 * @author Sebastian Riedel
 */
public final class ConstantTuple extends AbstractList<Constant> {


  /**
   * Array of constants that backs this tuple.
   */
  private final Constant[] constants;

  /**
   * Creates a tuple with the given constants.
   *
   * @param constants the constants in this tuple.
   */
  public ConstantTuple(final List<Constant> constants) {
    this.constants = constants.toArray(new Constant[constants.size()]);
  }

  /**
   * Creates a tuple with the constants given in the vararg array.
   *
   * @param constants a vararg array of constants.
   */
  ConstantTuple(final Constant... constants) {
    this.constants = new Constant[constants.length];
    System.arraycopy(constants, 0, this.constants, 0, constants.length);
  }

  /**
   * Returns the constant at the specified position in this list.
   *
   * @param index index of the constant to return.
   * @return the constant at the specified position in this list.
   * @throws IndexOutOfBoundsException if the given index is out of range
   *                                   (<tt>index &lt; 0 || index &gt;=
   *                                   size()</tt>).
   */
  public Constant get(final int index)
    throws IndexOutOfBoundsException {
    if (index >= constants.length) {
      throw new IndexOutOfBoundsException();
    }
    return constants[index];
  }

  /**
   * Convenience method that returns a UserConstant (if available at the given
   * index).
   *
   * @param index the position of the user constant to return.
   * @return the user constant at the given position.
   */
  public UserConstant getUserConstant(final int index) {
    return (UserConstant) constants[index];
  }

  /**
   * Returns the number of constants in this collection.  If the collection
   * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of constants in this tuple.
   */
  public int size() {
    return constants.length;
  }
}
