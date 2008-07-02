package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.AbstractSymbol;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Type;
import com.googlecode.thebeast.world.UserPredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A UserPredicate is an SQL based implementation of a UserType. It stores
 * constants using their integer id in an integer column in SQL tables.
 *
 * @author Sebastian Riedel
 */
final class SQLUserPredicate extends AbstractSymbol
  implements UserPredicate {

  /**
   * Stores the list of argument types.
   */
  private final ArrayList<SQLRepresentableType> argumentTypes;


  /**
   * Creates a new UserPredicate with the given name and types.
   *
   * @param name          the name of this predicate.
   * @param argumentTypes list of types which will be copied into this object.
   * @param signature     the signature this predicate should belong to.
   */
  SQLUserPredicate(final String name,
                   final List<SQLRepresentableType> argumentTypes,
                   final SQLSignature signature) {
    super(name, signature);
    this.argumentTypes = new ArrayList<SQLRepresentableType>(argumentTypes);
  }

  /**
   * Returns the argument type list of this predicate.
   *
   * @return an unmodifiable
   * @see Predicate#getArgumentTypes()
   */
  public List<? extends Type> getArgumentTypes() {
    return Collections.unmodifiableList(argumentTypes);
  }


  /**
   * A UserPredicate is not static.
   *
   * @return false.
   *
   * @see com.googlecode.thebeast.world.Predicate#isStatic()
   */
  public boolean isStatic() {
    return false;
  }

  /**
   * Returns the argument types as SQLRepresentable types.
   *
   * @return an unmodifiable list of SQLRepresentableType objects.
   */
  List<SQLRepresentableType> getSQLRepresentableArgumentTypes() {
    return Collections.unmodifiableList(argumentTypes);
  }

}
