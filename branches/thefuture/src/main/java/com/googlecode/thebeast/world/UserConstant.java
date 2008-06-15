package com.googlecode.thebeast.world;

/**
 * A UserConstant is a constant defined by the user.
 *
 * @author Sebastian Riedel
 */
public class UserConstant implements Constant {

  /**
   * The name of the constant as given by the user.
   */
  private String name;
  /**
   * The type of the constant.
   */
  private UserType type;
  /**
   * The id of the constant, assigned by the {@link
   * UserType}.
   */
  private int id;


  /**
   * Package visible constructor that creates a new UserConstant with the given
   * properties. Should only be called by {@link
   * UserType}.
   *
   * @param name the name of the constant.
   * @param type the type of the constant.
   * @param id   the id of the constant.
   */
  UserConstant(final String name, final UserType type, final int id) {
    this.name = name;
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
  public final int getId() {
    return id;
  }

  /**
   * Returns the {@link UserType} this
   * constant belongs to.
   *
   * @return UserType object that contains this constant.
   * @see UserType
   */
  public final Type getType() {
    return type;
  }

  /**
   * The name of this constant, which was given to it by the user at creation
   * time.
   *
   * @return a String object containing the name of this constant.
   */
  public final String getName() {
    return name;
  }
}
