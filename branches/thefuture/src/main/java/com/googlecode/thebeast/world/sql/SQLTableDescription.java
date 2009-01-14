package com.googlecode.thebeast.world.sql;

import java.util.Collections;
import java.util.List;

/**
 * A SQLTableDescription describes an SQL table that is used for storing FOL constant tuples. It provides the name of
 * the table, the names of the columns and the corresponding FOL type for each column.
 *
 * @author Sebastian Riedel
 */
final class SQLTableDescription {

    /**
     * The name of the table.
     */
    private final String tableName;
    /**
     * The FOL types it can store as tuples.
     */
    private final List<SQLRepresentableType> types;
    /**
     * The list of column names corresponding to the tuple argument indices.
     */
    private final List<String> columnNames;

    /**
     * Creates a new SQLTableDescription.
     *
     * @param tableName   the name of the table.
     * @param types       the FOL types it can store as tuples.
     * @param columnNames the list of column names corresponding to the tuple argument indices.
     */
    SQLTableDescription(final String tableName,
                        final List<SQLRepresentableType> types,
                        final List<String> columnNames) {
        this.tableName = tableName;
        this.types = types;
        this.columnNames = columnNames;
    }

    /**
     * Returns the FOL types the described table can store.
     *
     * @return A list of SQL representable FOL types, where the type at index i describes the type of constants at tuple
     *         position i.
     */
    List<SQLRepresentableType> getTypes() {
        return Collections.unmodifiableList(types);
    }

    /**
     * Return the name of column that represents the constant at tuple index argIndex.
     *
     * @param argIndex the arg index of a constant in a tuple.
     * @return the column name used for this arg index.
     */
    String getColumnName(final int argIndex) {
        return columnNames.get(argIndex);
    }

    /**
     * Returns the name of SQL table.
     *
     * @return the SQL table name.
     */
    String getTableName() {
        return tableName;
    }
}
