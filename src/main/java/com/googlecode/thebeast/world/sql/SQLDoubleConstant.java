package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.DoubleConstant;

/**
 * @author Sebastian Riedel
 */
class SQLDoubleConstant extends SQLRepresentableConstant
  implements DoubleConstant {

  /**
   * The double value the constant represents.
   */
  private final double value;

  /**
   * Creates a new SQLIntegerConstant with the given value and belonging to the
   * given type.
   *
   * @param type  the type the constant should belong to
   * @param value the value the constant represents.
   */
  SQLDoubleConstant(final SQLRepresentableType type,
                    final double value) {
    super(String.valueOf(value), type);
    this.value = value;
  }

  /**
   * Returns the double value.
   *
   * @return the double value this constant represents.
   */
  Object asSQLConstant() {
    return value;
  }

  /**
   * Returns the value this constant represents.
   *
   * @return the value this constant represents.
   */
  public double getValue() {
    return value;
  }

  /**
   * Return true iff this and the other object are both SQLDoubleConstant
   * objects and represent the same double value.
   *
   * @param o the other symbol.
   * @return true iff both objects are SQLDoubleConstant objects representing
   *         the same value.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    SQLDoubleConstant that = (SQLDoubleConstant) o;

    return value == that.value;

  }


  /**
   * Return hashcode based on double value.
   */
  @Override
  public int hashCode() {
    int result = super.hashCode();
    long temp;
    temp = value != +0.0d ? Double.doubleToLongBits(value) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}