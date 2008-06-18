package com.googlecode.thebeast.world;

import java.sql.SQLException;

/**
 * This exception is thrown whenever one of the classes in this package produces
 * an SQL exception. These exceptions show potential problems in this
 * implementation, not in the client.
 *
 * @author Sebastian Riedel
 */
public final class NestedSQLException extends RuntimeException {

  /**
   * The nested SQL exception.
   */
  private final SQLException sqlException;

  /**
   * Creates new nested SQLException.
   *
   * @param message      the message to show.
   * @param sqlException the nested exception.
   */
  NestedSQLException(final String message, final SQLException sqlException) {
    super(message,sqlException);
    this.sqlException = sqlException;
  }

  /**
   * Returns the nested exception.
   *
   * @return the nested exception.
   */
  public SQLException getSqlException() {
    return sqlException;
  }
}
