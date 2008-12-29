package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.IntegerConstant;

/**
 * @author Sebastian Riedel
 */
final class SQLIntegerConstant extends SQLRepresentableConstant
  implements IntegerConstant {

  /**
   * The integer value the constant represents.
   */
  private final int value;

  /**
   * Creates a new SQLIntegerConstant with the given value and belonging to the
   * given type.
   *
   * @param type  the type the constant should belong to
   * @param value the value the constant represents.
   */
  SQLIntegerConstant(final SQLRepresentableType type,
                     final int value) {
    super(String.valueOf(value), type);
    this.value = value;
  }

  /**
   * Returns the integer value.
   *
   * @return the integer value this constant represents.
   */
  Object asSQLConstant() {
    return value;
  }

  /**
   * Returns the value this constant represents.
   *
   * @return the value this constant represents.
   */
  public int getValue() {
    return value;
  }

  /**
   * Return true iff this and the other object are both SQLIntegerConstant
   * objects and represent the same integer value.
   *
   * @param o the other symbol.
   * @return true iff both objects are SQLIntegerConstant objects representing
   *         the same value.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    SQLIntegerConstant that = (SQLIntegerConstant) o;

    return value == that.value;

  }

  /**
   * Returns a hashcode based on the integer value.
   *
   * @return a hashcode based on the integer value.
   */
  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value;
    return result;
  }
}
