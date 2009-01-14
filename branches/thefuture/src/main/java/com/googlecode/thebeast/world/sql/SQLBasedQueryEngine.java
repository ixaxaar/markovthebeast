package com.googlecode.thebeast.world.sql;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.googlecode.thebeast.query.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * An SQLBasedQueryEngine executes queries in SQL based worlds.
 *
 * @author Sebastian Riedel
 */
final class SQLBasedQueryEngine {

    /**
     * Queries the given world wrt to the given query.
     *
     * @param query the clause to use as query.
     * @param world the world to query in.
     * @return the set of groundings for this clause.
     */
    NestedSubstitutionSet query(final Query query,
                                final SQLWorld world) {
        //create an sql queryString (or get one from cache)
        //get user predicate atoms from inner and outer and use their tables
        Multimap<Variable, String>
            var2columnName = new ArrayListMultimap<Variable, String>();
        Multimap<Term, String>
            term2columnName = new ArrayListMultimap<Term, String>();

        //build table list
        String tables = buildTableList(query, world,
            var2columnName, term2columnName);

        //build WHERE statement
        String where = buildWhere(var2columnName);

        //A map from variables to single column names
        Map<Variable, String>
            variable2singleColumn = new HashMap<Variable, String>();

        //Take the first column name assiocated with each variable.
        for (Variable var : var2columnName.keys()) {
            List<String> columnNames = (List<String>) var2columnName.get(var);
            variable2singleColumn.put(var, columnNames.get(0));
        }

        //build SELECT statement
        String select = buildSelect(variable2singleColumn);

        //build order by statement
        String orderBy = buildOrderBy(query);

        //build the whole queryString.
        final String queryString = where.equals("")
            ? String.format("SELECT %s FROM %s ORDER BY %s",
            select, tables, orderBy)
            : String.format("SELECT %s FROM %s WHERE %s ORDER BY %s",
            select, tables, where, orderBy);


        return new SQLNestedSubstitutionSet(query, world, queryString);
    }


    private static class SQLNestedSubstitutionSet implements NestedSubstitutionSet {

        /**
         * The clause that can be grounded with this set.
         */
        private final Query query;
        /**
         * The world to query.
         */
        private final SQLWorld world;
        /**
         * The SQL query string this grounding set is based on.
         */
        private final String queryString;


        /**
         * Creates a new grounding set for the given clause, world, and query.
         *
         * @param query       the clause that can be grounded with this set.
         * @param world       the world to query.
         * @param queryString the SQL query string to use.
         */
        private SQLNestedSubstitutionSet(final Query query,
                                         final SQLWorld world,
                                         final String queryString) {
            this.query = query;
            this.world = world;
            this.queryString = queryString;
        }

        /**
         * Prints out one grounding per row.
         *
         * @return the groundings as string.
         */
        public String toString() {
            StringBuffer result = new StringBuffer();
            for (NestedSubstitution g : this) {
                result.append(g.toString()).append("\n");
            }
            return result.toString();
        }

        /**
         * Returns the clause of this grounding set.
         *
         * @return the clause for the groundings in this set.
         * @see com.googlecode.thebeast.query.NestedSubstitutionSet#getQueryString()
         */
        public Query getQueryString() {
            return query;
        }

        /**
         * An iterator over all groudings based on the resultset of the sql query.
         *
         * @return an iterator that uses an sql result set.
         */
        public Iterator<NestedSubstitution> iterator() {
            try {
                final Statement st =
                    world.getSignature().getConnection().createStatement();
                final ResultSet resultSet = st.executeQuery(queryString);
                Multimap<Substitution, Substitution>
                    substitutions = new HashMultimap<Substitution, Substitution>();
                while (resultSet.next()) {
                    Substitution universal = new Substitution();
                    for (Variable var : query.getOuterVariables()) {
                        SQLRepresentableType type = (SQLRepresentableType) var.getType();
                        universal.put(var,
                            type.getConstantFromSQL(resultSet.getObject(var.getName())));
                    }
                    Substitution existential = new Substitution();
                    for (Variable var : query.getInnerVariables()) {
                        SQLRepresentableType type = (SQLRepresentableType) var.getType();
                        existential.put(var,
                            type.getConstantFromSQL(resultSet.getObject(var.getName())));
                    }
                    substitutions.put(universal, existential);
                }
                List<NestedSubstitution> result = new ArrayList<NestedSubstitution>();
                for (Substitution substitution : substitutions.keys()) {
                    result.add(new NestedSubstitution(substitution,
                        (Set<Substitution>) substitutions.get(substitution)));
                }
                return result.iterator();

            } catch (SQLException e) {
                throw new NestedSQLException("Couldn't execute SQL", e);
            }
        }
    }


    /**
     * Creates the table list of a query for the given clause in the given world. As side product it creates mappings from
     * variables to column names and from general terms (without variables )to column names.
     *
     * @param clause          the clause for which to create the table list.
     * @param world           the world for which to create the table list.
     * @param var2columnName  a mapping from variables to table alias + column name strings that represent the variable.
     * @param term2columnName a mapping from terms (without variables) to table alias + column name strings that represent
     *                        the term.
     * @return the table list clause.
     */
    private String buildTableList(final Query clause,
                                  final SQLWorld world,
                                  final Multimap<Variable, String> var2columnName,
                                  final Multimap<Term, String> term2columnName) {
        StringBuffer tables = new StringBuffer();
        int tableIndex = 0;
        //process user predicates (which are stored using tables.
        //build table-list
        for (Atom a : clause.getAll()) {
            if (a.getPredicate() instanceof SQLUserPredicate) {
                SQLUserPredicate pred = (SQLUserPredicate) a.getPredicate();
                SQLRelation rel = world.getSQLRelation(pred);
                SQLTableDescription table = rel.getTableDescription();
                String tableAlias = pred.getName() + tableIndex;
                if (tableIndex > 0) {
                    tables.append(", ");
                }
                tables.append(String.format("%s AS %s",
                    table.getTableName(), tableAlias));
                int argIndex = 0;
                for (Term term : a.getArguments()) {
                    String columnName = tableAlias + "."
                        + table.getColumnName(argIndex);
                    if (term instanceof Variable) {
                        var2columnName.put((Variable) term, columnName);
                    } else {
                        term2columnName.put(term, columnName);
                    }
                    ++argIndex;
                }
                ++tableIndex;
            }
        }
        return tables.toString();
    }

    /**
     * Builds the order by clause using the universal variables of the clause. This assumes that the result table has a
     * column for each variable with the name of the variable.
     *
     * @param clause the clause for which the order statement has to be built.
     * @return an order by clause (without ORDER BY).
     */
    private String buildOrderBy(final Query clause) {
        StringBuffer orderBy = new StringBuffer();
        int orderByIndex = 0;
        for (Variable var : clause.getOuterVariables()) {
            if (orderByIndex++ > 0) {
                orderBy.append(", ");
            }
            orderBy.append(var.getName());
        }
        return orderBy.toString();
    }

    /**
     * Build the select statement based on a mapping from variables to column names.
     *
     * @param var2columnName the mapping from variables to column names.
     * @return a select clause (without SELECT).
     */
    private String buildSelect(final Map<Variable, String> var2columnName) {
        StringBuffer select = new StringBuffer();
        int selectIndex = 0;
        for (Variable var : var2columnName.keySet()) {
            String first = var2columnName.get(var);
            if (selectIndex > 0) {
                select.append(", ");
            }
            select.append(String.format("%s AS %s", first, var.getName()));
            ++selectIndex;
        }
        return select.toString();
    }

    /**
     * Builds where clause for a select statement based on a mapping from variables to column names.
     *
     * @param var2columnName mapping from variables to column names.
     * @return a where clause.
     */
    private String buildWhere(final Multimap<Variable, String> var2columnName) {
        int whereIndex = 0;
        StringBuffer where = new StringBuffer();
        for (Variable var : var2columnName.keys()) {
            List<String> columnNames = (List<String>) var2columnName.get(var);
            String first = columnNames.get(0);
            for (int i = 1; i < columnNames.size(); ++i) {
                String other = columnNames.get(i);
                if (whereIndex++ > 0) {
                    where.append(" AND ");
                }
                where.append(String.format("%s = %s", first, other));
            }
        }
        return where.toString();
    }


}
