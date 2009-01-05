package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.Constant;
import com.googlecode.thebeast.world.MutableRelation;
import com.googlecode.thebeast.world.RelationListener;
import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.Type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An SQLRelation is a SQL based implementation of the Relation interface. It
 * stores its tuples in a database table where each tuple argument index
 * corresponds to one column of the table.
 *
 * @author Sebastian Riedel
 */
final class SQLRelation extends AbstractCollection<Tuple>
  implements MutableRelation {


  /**
   * The specification of the table that backs this relation.
   */
  private final SQLTableDescription tableDescription;


  /**
   * The predicate for which this relation holds tuples.
   */
  private final SQLUserPredicate userPredicate;

  /**
   * The size of this relation.
   */
  private int size = 0;

  /**
   * The signature this relation uses symbols of.
   */
  private final SQLSignature signature;

  /**
   * The list of listeners of this relation.
   */
  private final ArrayList<RelationListener>
    listeners = new ArrayList<RelationListener>();

  /**
   * Creates a new Relation using the SQL table specified in the
   * tableDescription.
   *
   * @param tableDescription the specification of the table to back this
   *                         relation.
   * @param userPredicate    the predicate for which this relation holds
   *                         tuples.
   */
  SQLRelation(final SQLTableDescription tableDescription,
              final SQLUserPredicate userPredicate) {
    this.tableDescription = tableDescription;
    this.userPredicate = userPredicate;
    signature = (SQLSignature) tableDescription.getTypes().get(0).getSignature();
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
  public boolean contains(final Tuple tuple) {
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
      throw new NestedSQLException("Couldn't check whether relation"
        + " constains " + tuple, e);
    }

  }


  /**
   * Returns an iterator over the tuples of this relation. Not synchronized.
   *
   * @return an Iterator.
   */
  public Iterator<Tuple> iterator() {
    try {
      Statement st = signature.getConnection().createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);
      final ResultSet resultSet =
        st.executeQuery(String.format("SELECT * FROM %s",
          tableDescription.getTableName()));

      resultSet.first();
      return new Iterator<Tuple>() {
        /**
         * Method hasNext checks whether the pointer of the current result
         * set is behind the end of the result set.
         * @return boolean if the end has been reached.
         */
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
        public Tuple next() {
          try {
            Constant[] constants =
              new Constant[tableDescription.getTypes().size()];
            for (int i = 0; i < constants.length; ++i) {
              constants[i] = tableDescription.getTypes().get(i).
                getConstantFromSQL(resultSet.getObject(i + 1));
            }
            resultSet.next();
            return new Tuple(constants);
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
   * {@inheritDoc}
   */
  public boolean addTuple(Object... args) {
    return add(new Tuple(userPredicate, args));
  }

  /**
   * {@inheritDoc}
   */
  public boolean containsTuple(Object... args) {
    return contains(new Tuple(userPredicate,args));
  }

  /**
   * Adds a tuple to the relation by using an updatable result set.
   *
   * @param tuple the tuple to add.
   * @see com.googlecode.thebeast.world.MutableRelation#add(Tuple)
   */
  public boolean add(final Tuple tuple) {
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
        listener.tupleAdded(this, tuple);
      }
      return true;
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
    for (Tuple tuple : this) {
      result.append(tuple).append("\n");
    }
    return result.toString();
  }
}
