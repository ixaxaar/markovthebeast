package com.googlecode.thebeast.world;

/**
 * This exception is thrown when an iterator is requested from a type which is
 * not iterable.
 *
 * @author Sebastian Riedel
 */
public class TypeNotIterableException extends RuntimeException {

  /**
   * The type that threw the exception.
   */
  private final Type type;

  /**
   * Constructor TypeNotIterableException creates a new TypeNotIterableException
   * instance.
   *
   * @param type of type Type is the type which is not iterable but for which an
   *             iterator was requested.
   */
  public TypeNotIterableException(Type type) {
    super("Type " + type + " is not iterable");
    this.type = type;
  }

  /**
   * Returns the type that threw the exception.
   *
   * @return the type which is not iterable but for which an iterator  was
   *         requested.
   */
  public Type getType() {
    return type;
  }
}
