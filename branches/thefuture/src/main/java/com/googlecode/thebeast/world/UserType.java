package com.googlecode.thebeast.world;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * A UserType is a collection of constants created by the user. Note that the
 * UserType object creates and owns all its constants. Also note that a UserType
 * can only be created using {@link Signature#createPredicate(String,
 * java.util.List)}.
 *
 * <p>By default there is no order defined on constants of this type. This can
 * be achieved by defining a corresponding (user)predicate.
 *
 * @author Sebastian Riedel
 * @see com.googlecode.thebeast.world.UserConstant
 * @see com.googlecode.thebeast.world.Signature
 */
public final class UserType extends SQLRepresentableType {


  /**
   * Indicates whether type can be extended on the fly.
   */
  private final boolean extendable;

  /**
   * Contains a mapping from unique ids to constants.
   */
  private final LinkedHashMap<Integer, UserConstant>
    id2constant = new LinkedHashMap<Integer, UserConstant>();

  /**
   * Contains a mapping from constant names to constants.
   */
  private final LinkedHashMap<String, UserConstant>
    constants = new LinkedHashMap<String, UserConstant>();


  /**
   * Creates a type with the given name. Usually called by the {@link
   * Signature}.
   *
   * @param name       the name of the type.
   * @param extendable true iff calls to getConstant with an unknown name should
   *                   create new constants with the unknown names.
   * @param signature  the signature this type belongs to.
   */
  UserType(final String name, final boolean extendable,
           final Signature signature) {
    super(name, signature);
    this.extendable = extendable;
  }


  /**
   * Returns an unmodifiable view on the set of constants.
   *
   * @return an unmodifiable collection of the constants contained in this
   *         type.
   */
  public Collection<UserConstant> getConstants() {
    return Collections.unmodifiableCollection(constants.values());
  }


  /**
   * Creates a new UserConstant with the given name, assigns an id to it and
   * links it to this type.
   *
   * @param name the name of the constant.
   * @return a UserConstant with the given name and this object as its type.
   */
  public UserConstant createConstant(final String name) {
    UserConstant constant =
      new UserConstant(name, this, constants.size(), getSignature());
    getSignature().registerSymbol(constant);
    constants.put(name, constant);
    id2constant.put(constant.getId(), constant);
    return constant;
  }

  /**
   * Returns true iff this type can be extended via the getConstant method. If
   * so, whenever getConstant is called with an unknown name a constant with
   * this name is created.
   *
   * @return a boolean indicating whether this type is extendable.
   */
  public boolean isExtendable() {
    return extendable;
  }

  /**
   * Returns the constant with the given name. If the type is extendable a new
   * constant with the given name is created if no constant with such name
   * existed before.
   *
   * @param name the name of the constant.
   * @return the constant with the given name.
   * @throws ConstantNameNotInTypeException if there is no constant with the
   *                                        given name and this type is not
   *                                        extendable.
   */
  public UserConstant getConstant(final String name)
    throws ConstantNameNotInTypeException {
    UserConstant constant = constants.get(name);
    if (constant == null) {
      if (extendable) {
        return createConstant(name);
      } else {
        throw new ConstantNameNotInTypeException(name, this);
      }

    } else {
      return constant;
    }
  }

  /**
   * Returns constant with the specific id.
   *
   * @param id the id of the constant to return.
   * @return the constant with the given id.
   * @throws ConstantIdNotInTypeException if there is no constant with the given
   *                                      id in this type.
   */
  public UserConstant getConstant(final int id)
    throws ConstantIdNotInTypeException {
    UserConstant constant = id2constant.get(id);
    if (constant == null) {
      throw new ConstantIdNotInTypeException(id, this);
    } else {
      return constant;
    }
  }

  /**
   * Returns the SQL type to represent user constants. UserType uses integers
   * (the constant ids) to represent its constants.
   *
   * @return the SQL column type to represent objects of this type.
   */
  String asSQLType() {
    return "integer";
  }

  /**
   * Get the constant denoted by the given SQL representation.
   *
   * @param representation the Integer object that represents the id of the
   *                       constant to return.
   * @return the constant denoted by the given representation.
   */
  Constant getConstantFromSQL(final Object representation) {
    return getConstant((Integer) representation);
  }
}
