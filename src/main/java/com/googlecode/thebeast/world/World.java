package com.googlecode.thebeast.world;

import java.sql.Statement;
import java.sql.SQLException;

/**
 * <p>A World represents a possible world or Herbrand Model. It contains a set
 * of ground atoms, either added manually, through solving, or with built-in
 * predicates.</p>
 *
 * <p>This implementation is based on using an SQL database as underlying data
 * store.</p>
 *
 * @author Sebastian Riedel
 */
public final class World {

  private Signature signature;
  private int id;

  /**
   * Create a new world belonging to the given signature and with the given id.
   *
   * @param signature signature this world belongs to.
   * @param id        unique number identifying this world.
   */
  World(Signature signature, int id) {
    this.signature = signature;
    this.id = id;
    try {
      Statement st = signature.getConnection().createStatement();
      st.executeUpdate("DROP TABLE Employee11");
      String table = "CREATE TABLE Employee11(Emp_code integer, Emp_name varchar(10))";
      st.executeUpdate(table);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    System.out.println("Table creation process successfully!");
  }

  /**
   * Returns id of this world.
   *
   * @return a unique integer identifying this world among all other worlds
   *         belonging to the same signature.
   */
  public int getId() {
    return id;
  }

  /**
   * Return the signature of this world.
   *
   * @return the signature this world belongs to.
   */
  public Signature getSignature() {
    return signature;
  }
}
