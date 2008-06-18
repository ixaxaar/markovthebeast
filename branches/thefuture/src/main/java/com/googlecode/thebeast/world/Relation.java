package com.googlecode.thebeast.world;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Relation object represents a relation over constants. It contains {@link
 * com.googlecode.thebeast.world.ConstantTuple} objects. It can be modified and
 * new tuples can be inserted. The only way to get a Relation object is via the
 * {@link World#getRelation(UserPredicate)} method.
 *
 * @author Sebastian Riedel
 */
public final class Relation extends AbstractCollection<ConstantTuple> {


  /**
   * The specification of the table that backs this relation.
   */
  private final SQLTableDescription tableDescription;


  /**
   * The size of this relation.
   */
  private int size = 0;

  /**
   * The signature this relation uses symbols of.
   */
  private final Signature signature;


  /**
   * The list of listeners of this relation.
   */
  private final ArrayList<RelationListener>
    listeners = new ArrayList<RelationListener>();

  /**
   * Creates a new Relation backed by an updatable result set.
   *
   * @param tableDescription the specification of the table to back this
   *                         relation.
   */
  Relation(final SQLTableDescription tableDescription) {
    this.tableDescription = tableDescription;
    signature = tableDescription.getTypes().get(0).getSignature();
  }

  /**
   * Adds a listener to this relation that will be informed whenever a new tuple
   * was added.
   *
   * @param listener the listener to add.
   */
  public void addListener(final RelationListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener and stops sending events to it.
   *
   * @param listener the listener to remove.
   */
  public void removeListener(final RelationListener listener) {
    listeners.remove(listener);
  }

  /**
   * Returns the description of the table that backs this relation.
   *
   * @return an SQLTableDescription describing the backing table of this
   *         relation.
   */
  SQLTableDescription getTableDescription() {
    return tableDescription;
  }

  /**
   * Checks whether the tuple is contained in this relation.
   *
   * @param tuple the tuple for which we test whether it is contained in this
   *              relation.
   * @return true iff the relation contains the tuple.
   */
  public boolean contains(ConstantTuple tuple) {
    try {
      Statement st = signature.getConnection().createStatement();
      StringBuffer condition = new StringBuffer();
      int index = 0;
      for (Constant constant : tuple) {
        if (index > 0) {
          condition.append(" AND ");
        }
        condition.append(String.format("%s = %s",
          tableDescription.getColumnName(index),
          ((SQLRepresentableConstant) constant).asSQLConstant()));
        ++index;
      }
      ResultSet resultSet = st.executeQuery(
        String.format("SELECT * FROM %s WHERE %s",
          tableDescription.getTableName(),
          condition));
      return !resultSet.isAfterLast();
    } catch (SQLException e) {
      throw new NestedSQLException("Couldn't check whether relation" +
        " constains " + tuple, e);
    }

  }


  /**
   * Returns an iterator over the tuples of this relation. Not synchronized.
   *
   * @return an Iterator.
   */
  public Iterator<ConstantTuple> iterator() {
    try {
      Statement st = signature.getConnection().createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);
      final ResultSet resultSet =
        st.executeQuery(String.format("SELECT * FROM %s",
          tableDescription.getTableName()));

      resultSet.first();
      return new Iterator<ConstantTuple>() {
        public boolean hasNext() {
          try {
            return !resultSet.isAfterLast();
          } catch (SQLException e) {
            throw new NestedSQLException("Iterator problem: ", e);
          }
        }

        /**
         * Returns the next tuple in the result set. Moves the cursor.
         * @return a new tuple object representing the next row in the result
         * set.
         */
        public ConstantTuple next() {
          try {
            Constant[] constants =
              new Constant[tableDescription.getTypes().size()];
            for (int i = 0; i < constants.length; ++i) {
              constants[i] = tableDescription.getTypes().get(i).
                getConstantFromSQL(resultSet.getObject(i + 1));
            }
            resultSet.next();
            return new ConstantTuple(constants);
          } catch (SQLException e) {
            throw new NestedSQLException("Iterator problem: ", e);
          }
        }

        /**
         * Does nothing right now.
         */
        public void remove() {

        }
      };
    } catch (SQLException e) {
      throw new NestedSQLException("Iterator problem: ", e);
    }
  }

  /**
   * Returns the number of tuples in this relation.
   *
   * @return the count of tuples in this relation.
   */
  public int size() {
    return size;
  }


  /**
   * Returns the argument types of the tuples of this relation.
   *
   * @return the argument types.
   */
  public List<? extends Type> getTypes() {
    return tableDescription.getTypes();
  }

  /**
   * Adds a tuple to the relation.
   *
   * @param tuple the tuple to add.
   */
  public void addTuple(final ConstantTuple tuple) {
    try {
      Statement st = signature.getConnection().createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);
      final ResultSet resultSet =
        st.executeQuery(String.format("SELECT * FROM %s",
          tableDescription.getTableName()));
      resultSet.moveToInsertRow();
      for (int i = 0; i < tuple.size(); ++i) {
        SQLRepresentableConstant sqlConstant =
          (SQLRepresentableConstant) tuple.get(i);
        resultSet.updateObject(i + 1, sqlConstant.asSQLConstant());
      }
      resultSet.insertRow();
      ++size;
      for (RelationListener listener : listeners) {
        listener.tupleAdded(this,tuple);
      }
    } catch (SQLException e) {
      throw new NestedSQLException("Problem when adding tuple: ", e);
    }
  }

  /**
   * Returns the tuples as list of rows.
   *
   * @return a String representation of this relation.
   */
  public String toString() {
    StringBuffer result = new StringBuffer();
    for (ConstantTuple tuple : this) {
      result.append(tuple).append("\n");
    }
    return result.toString();
  }
}
