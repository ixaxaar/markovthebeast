package com.googlecode.thebeast.clause;

import com.googlecode.thebeast.world.Type;

/**
 * A Variable object represents a First Order Logic variable.
 *
 * @author Sebastian Riedel
 */
public final class Variable implements Term {

  /**
   * The name of the variable.
   */
  private final String name;

  /**
   * The type of the variable.
   */
  private final Type type;

  /**
   * Creates a new variable with the given name and type.
   *
   * @param name the name of the variable.
   * @param type the type of the variable.
   */
  Variable(final String name, final Type type) {
    this.name = name;
    this.type = type;
  }

  /**
   * Method getName returns the name of this variable.
   *
   * @return the name (type String) of this variabe.
   */
  public String getName() {
    return name;
  }

  /**
   * Return the type of this variable.
   *
   * @return the type of this variable.
   * @see com.googlecode.thebeast.clause.Term#getType()
   */
  public Type getType() {
    return type;
  }
}
