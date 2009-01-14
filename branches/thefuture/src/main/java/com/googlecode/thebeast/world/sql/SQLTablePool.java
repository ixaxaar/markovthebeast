package com.googlecode.thebeast.world.sql;

import com.google.common.collect.ArrayListMultimap;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A SQLTablePool maintains a list of sql tables. By requesting tables from this pool is is possible to reuse tables
 * that have been created before for the same type list.
 *
 * @author Sebastian Riedel
 */
final class SQLTablePool {

    /**
     * The signature that owns this pool.
     */
    private final SQLSignature signature;

    /**
     * The id the pool will assign to the next created table.
     */
    private int currentId = 0;

    /**
     * A logger for this SQL table pool
     */
    private static Logger logger = LoggerFactory.getLogger(SQLTablePool.class);

    /**
     * A multimap from type lists to table descriptions.
     */
    private final ArrayListMultimap<List<? extends Type>, SQLTableDescription>
        tables = new ArrayListMultimap<List<? extends Type>, SQLTableDescription>();

    /**
     * Creates a new SQLTablePool for the given signature.
     *
     * @param signature the signature to get the SQL connection from.
     */
    SQLTablePool(final SQLSignature signature) {
        this.signature = signature;
        try {
            DatabaseMetaData metaData = signature.getConnection().getMetaData();
            ResultSet names = metaData.getTables(
                null, null, null, new String[]{"TABLE"});
            while (names.next()) {
                Statement st = signature.getConnection().createStatement();
                st.executeUpdate(String.format("DROP TABLE %s",
                    names.getString("TABLE_NAME")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return the signature this pool belongs to and which provided the SQL connection.
     *
     * @return the signature belonging to this pool.
     */
    Signature getSignature() {
        return signature;
    }

    /**
     * Request a new table for the given sequence of argument types. The client how requested the table can be sure that
     * the same table will not be offered to any other client until the table is released again.
     *
     * @param types the types of the tuple arguments in corresponding order.
     * @return a description of a table that can be used to store tuples of the given type sequence.
     */
    SQLTableDescription requestTable(final List<SQLRepresentableType> types) {
        List<SQLTableDescription> descriptions = tables.get(types);
        if (descriptions.size() == 0) {
            return createTable(types);
        } else {            
            return descriptions.remove(descriptions.size() - 1);
        }
    }

    /**
     * Creates a new table for the given type sequence.
     *
     * @param types the types of the tuple arguments that should be represented as table rows.
     * @return the description of table that has been created to accomodate tuples of the given type.
     */
    private SQLTableDescription createTable(
        final List<SQLRepresentableType> types) {
        try {
            Statement st = signature.getConnection().createStatement();
            String tableName = "table_" + currentId++;
            StringBuffer createTable = new StringBuffer();
            StringBuffer primaryKeyString = new StringBuffer();
            ArrayList<String> columnNames = new ArrayList<String>();
            int argIndex = 0;
            for (Type type : types) {
                String columnName = "arg" + argIndex;
                columnNames.add(columnName);
                SQLRepresentableType sqlType = (SQLRepresentableType) type;
                if (argIndex > 0) {
                    createTable.append(", ");
                    primaryKeyString.append(", ");
                }
                primaryKeyString.append("arg").append(argIndex);
                createTable.append(columnName).append(" ");
                createTable.append(sqlType.asSQLType());
                ++argIndex;
            }

            final String createTableString = String.format("CREATE TABLE %s(%s, PRIMARY KEY (%s))",
                tableName, createTable.toString(), primaryKeyString);
            st.executeUpdate(createTableString);
            logger.debug("Created table using the command " + createTableString);
            return new SQLTableDescription(tableName, types, columnNames);
        } catch (SQLException e) {
            throw new NestedSQLException("Couldn't create a table for the "
                + "specified type " + types, e);
        }

    }

    /**
     * Releases the database table with the given description. After the client has called this method the table might be
     * reused by other clients.
     *
     * @param description the description of the table to release.
     */
    void releaseTable(final SQLTableDescription description) {
        tables.get(description.getTypes()).add(description);
    }

}
