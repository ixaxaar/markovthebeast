package com.googlecode.thebeast.world;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * A UserType is a collection of constants created by the user. Note that the
 * UserType object creates and owns all its constants.
 *
 * @author Sebastian Riedel
 * @see UserConstant
 */
public class UserType implements Type {


  /**
   * Contains a mapping from constant names to constants.
   */
  private LinkedHashMap<String, UserConstant>
    constants = new LinkedHashMap<String, UserConstant>();

  /**
   * The name of the type.
   */
  private String name;

  /**
   * Creates a type with the given name. Usually called by the {@link
   * Signature}.
   *
   * @param name the name of the type.
   */
  UserType(final String name) {
    this.name = name;
  }

  /**
   * Returns the name of this type as given by the user.
   *
   * @return a string representing the name of this type.
   */
  public final String getName() {
    return name;
  }

  /**
   * Returns an unmodifiable view on the set of constants.
   *
   * @return an unmodifiable collection of the constants contained in this
   *         type.
   */
  public final Collection<UserConstant> getConstants() {
    return Collections.unmodifiableCollection(constants.values());
  }


  /**
   * Creates a new UserConstant with the given name, assigns an id to it and
   * links it to this type.
   *
   * @param name the name of the constant.
   * @return a UserConstant with the given name and this object as its type.
   */
  public final UserConstant createConstant(final String name) {
    UserConstant constant = new UserConstant(name, this, constants.size());
    constants.put(name, constant);
    return constant;
  }


}
