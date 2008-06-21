package com.googlecode.thebeast.world;

/**
 * This exception is raised whenever a constant  is requested from a type by
 * name.
 *
 * @author Sebastian Riedel
 */
public final class ConstantNameNotInTypeException extends RuntimeException {

  /**
   * The name that was used to request the constant.
   */
  private final String constantName;
  /**
   * The type that got the request.
   */
  private final Type type;

  /**
   * Creates an exception for the given constant name and type.
   *
   * @param constantName the name that was used to request the constant.
   * @param type         the type which got the request.
   */
  public ConstantNameNotInTypeException(final String constantName,
                                 final Type type) {
    super("There is no constant with name " + constantName + " in type "
      + type.getName());
    this.constantName = constantName;
    this.type = type;
  }

  /**
   * Returns the string that was used to request a constant from a type that
   * does not contain a constant with this name.
   *
   * @return the name that was used to request the constant.
   */
  public String getConstantName() {
    return constantName;
  }

  /**
   * Returns the type which got the request for a constant with the given name.
   *
   * @return the type which got the request.
   */
  public Type getType() {
    return type;
  }
}
