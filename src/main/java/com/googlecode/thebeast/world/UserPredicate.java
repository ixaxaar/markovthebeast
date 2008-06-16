package com.googlecode.thebeast.world;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A UserPredicate is a predicate that is defined by the user. Also, in contrast
 * to a built-in predicate it can have different relations attached to it in
 * different possible worlds.
 * <p/>
 * <p>UserPredicates can only be created by {@link
 * com.googlecode.thebeast.world.Signature}.
 *
 * @author Sebastian Riedel
 */
public final class UserPredicate extends AbstractSymbol implements Predicate {

  /**
   * Stores the list of argument types.
   */
  private final ArrayList<Type> argumentTypes;


  /**
   * Creates a new UserPredicate with the given name and types.
   *
   * @param name          the name of this predicate.
   * @param argumentTypes list of types which will be copied into this object.
   * @param signature     the signature this predicate should belong to.
   */
  UserPredicate(final String name, final List<Type> argumentTypes,
                final Signature signature) {
    super(name, signature);
    this.argumentTypes = new ArrayList<Type>(argumentTypes);
  }

  /**
   * Returns the argument type list of this predicate.
   *
   * @return an unmodifiable
   */
  public List<Type> getArgumentTypes() {
    return Collections.unmodifiableList(argumentTypes);
  }
}
