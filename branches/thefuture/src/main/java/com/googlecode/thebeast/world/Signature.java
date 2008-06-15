package com.googlecode.thebeast.world;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * A Signature maintains a set of types, predicates and functions. In
 * particular, it provides a mapping from their String names to the
 * corresponding objects.
 *
 * @author Sebastian Riedel
 * @see Type
 */
public class Signature {

  /**
   * A map from type names to types. This map contains user types as well as
   * built-in types.
   *
   * @see UserType
   */
  private LinkedHashMap<String, Type> types = new LinkedHashMap<String, Type>();

  /**
   * Creates a new UserType with the given name.
   *
   * @param name the name of the type.
   * @return a UserType with the given name.
   */
  public final UserType createType(final String name) {
    UserType type = new UserType(name);
    types.put(name, type);
    return type;
  }

  /**
   * Returns the set of all types this signature maintains.
   *
   * @return a collection of all types in this signature. Iterating over this
   *         collection maintains the order of type creation.
   */
  public final Collection<Type> getTypes() {
    return Collections.unmodifiableCollection(types.values());
  }

}
