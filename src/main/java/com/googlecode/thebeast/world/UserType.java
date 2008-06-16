package com.googlecode.thebeast.world;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * A UserType is a collection of constants created by the user. Note that the
 * UserType object creates and owns all its constants. Also note that a UserType
 * can only be created using {@link
 * com.googlecode.thebeast.world.Signature#createPredicate(String,
 * java.util.List)}.
 *
 * <p>By default there is no order defined on constants of this type. This can
 * be achieved by defining a corresponding (user)predicate.
 *
 * @author Sebastian Riedel
 * @see com.googlecode.thebeast.world.UserConstant
 * @see com.googlecode.thebeast.world.Signature
 */
public final class UserType extends AbstractSymbol implements Type {


  /**
   * Contains a mapping from constant names to constants.
   */
  private final LinkedHashMap<String, UserConstant>
    constants = new LinkedHashMap<String, UserConstant>();


  /**
   * Creates a type with the given name. Usually called by the {@link
   * Signature}.
   *
   * @param name      the name of the type.
   * @param signature the signature this type belongs to.
   */
  UserType(final String name, final Signature signature) {
    super(name, signature);
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
    return constant;
  }


}
