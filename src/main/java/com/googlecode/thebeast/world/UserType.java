package com.googlecode.thebeast.world;

import java.util.Collection;

/**
 * A UserType is a collection of constants created by the user. Note that the
 * UserType object creates and owns all its constants. Also note that a UserType
 * can only be created using {@link Signature#createType(String, boolean)}
 * method (or its helper methods).
 *
 * <p>By default there is no order defined on constants of this type. This can
 * be achieved by defining a corresponding (user)predicate.
 *
 * @author Sebastian Riedel
 */
public interface UserType extends Symbol, Type {
  /**
   * Returns an unmodifiable view on the set of constants.
   *
   * @return an unmodifiable collection of the constants contained in this
   *         type.
   */
  Collection<? extends UserConstant> getConstants();

  /**
   * Creates a new UserConstant with the given name, assigns an id to it and
   * links it to this type.
   *
   * @param name the name of the constant.
   * @return a UserConstant with the given name and this object as its type.
   */
  UserConstant createConstant(String name);

  /**
   * Returns true iff this type can be extended via the getConstant method. If
   * so, whenever getConstant is called with an unknown name a constant with
   * this name is created.
   *
   * @return a boolean indicating whether this type is extendable.
   */
  boolean isExtendable();

  /**
   * Returns the constant with the given name. If the type is extendable a new
   * constant with the given name is created if no constant with such name
   * existed before.
   *
   * @param name the name of the constant.
   * @return the constant with the given name.
   * @throws com.googlecode.thebeast.world.ConstantNameNotInTypeException
   *          if there is no constant with the given name and this type is not
   *          extendable.
   */
  UserConstant getConstant(String name)
    throws ConstantNameNotInTypeException;

  /**
   * Returns constant with the specific id.
   *
   * @param id the id of the constant to return.
   * @return the constant with the given id.
   * @throws com.googlecode.thebeast.world.ConstantIdNotInTypeException
   *          if there is no constant with the given id in this type.
   */
  UserConstant getConstant(int id)
    throws ConstantIdNotInTypeException;
}
