package com.googlecode.thebeast.world;

/**
 * A UserConstant is a constant defined by the user.
 *
 * @author Sebastian Riedel
 */
public final class UserConstant extends AbstractSymbol implements Constant {


  /**
   * The type of the constant.
   */
  private final UserType type;

  /**
   * The id of the constant, assigned by the {@link UserType}.
   */
  private final int id;


  /**
   * Package visible constructor that creates a new UserConstant with the given
   * properties. Should only be called by {@link UserType}.
   *
   * @param name      the name of the constant.
   * @param type      the type of the constant.
   * @param id        the id of the constant.
   * @param signature the signature of this constant.
   */
  UserConstant(final String name, final UserType type,
               final int id, final Signature signature) {
    super(name, signature);
    this.type = type;
    this.id = id;
  }


  /**
   * A UserConstant has an integer id the can be used to represent the constant
   * more compactly than by its name. The id is assigned by the UserType that
   * created and owns this constant.
   *
   * @return the id number of this constant.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the {@link UserType} this constant belongs to.
   *
   * @return UserType object that contains this constant.
   * @see UserType
   */
  public Type getType() {
    return type;
  }


}
