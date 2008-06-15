package com.googlecode.thebeast.world;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
   * Connection to database that is used to store ground atoms.
   */
  private Connection connection;

  /**
   * Creates a new signature and opens a connection to the H2 database.
   */
  Signature() {
    try {
      Class.forName("org.h2.Driver");
      connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Provides a signature-wide database connection to classes of this package.
   *
   * @return the database connection of this signature.
   */
  final Connection getConnection() {
    return connection;
  }


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
   * Returns the type corresponding to the given type name.
   *
   * @param name the name of the type to return.
   * @return either a built-in type of a {@link
   * com.googlecode.thebeast.world.UserType}
   */
  public final Type getType(final String name) {
    return types.get(name);
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
