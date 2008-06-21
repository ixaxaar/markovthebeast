package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.Type;
import com.googlecode.thebeast.world.UserConstant;
import com.googlecode.thebeast.world.UserType;

/**
 * A UserConstant is a constant defined by the user.
 *
 * @author Sebastian Riedel
 */
final class SQLUserConstant extends SQLRepresentableConstant
  implements UserConstant {

  /**
   * The type of the constant.
   */
  private final UserType type;

  /**
   * The id of the constant, assigned by the {@link com.googlecode.thebeast.world.sql.SQLUserType}.
   */
  private final int id;


  /**
   * Package visible constructor that creates a new UserConstant with the given
   * properties. Should only be called by {@link com.googlecode.thebeast.world.sql.SQLUserType}.
   *
   * @param name      the name of the constant.
   * @param type      the type of the constant.
   * @param id        the id of the constant.
   * @param signature the signature of this constant.
   */
  SQLUserConstant(final String name, final UserType type,
                  final int id, final SQLSignature signature) {
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
   * Returns the UserType type of this constant.
   *
   * @return the type of this UserConstant as a UserType.
   */
  public UserType getUserType() {
    return type;
  }

  /**
   * Returns the {@link com.googlecode.thebeast.world.sql.SQLUserType} this
   * constant belongs to.
   *
   * @return UserType object that contains this constant.
   * @see com.googlecode.thebeast.world.sql.SQLUserType
   */
  public Type getType() {
    return type;
  }


  /**
   * Return an SQL representation of this constant.
   *
   * @return an Integer object representing the id number of this constant.
   * @see com.googlecode.thebeast.world.sql.SQLUserType#asSQLType()
   */
  Object asSQLConstant() {
    return id;
  }


}
