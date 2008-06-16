package com.googlecode.thebeast.world;

/**
 * @author Sebastian Riedel
 */

/**
 * Exception thrown when a type is requested by name and there exist no such
 * type.
 */
public final class TypeNotInSignatureException
  extends RuntimeException {

  /**
   * The name of the requested type.
   */
  private final String typeName;

  /**
   * Creates Exception for given type name.
   *
   * @param typeName the name of the type that was requested.
   */
  TypeNotInSignatureException(final String typeName) {
    super("There is no type with name " + typeName + " in this signature");
    this.typeName = typeName;
  }

  /**
   * The name of the type the client wanted to get.
   *
   * @return a string containing the name of the nonexistent type.
   */
  public String getTypeName() {
    return typeName;
  }

}
