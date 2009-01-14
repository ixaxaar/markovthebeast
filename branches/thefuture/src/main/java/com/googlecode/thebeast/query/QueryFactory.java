package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.WorldUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A Factory for atoms and queries. All atoms and queries have to be created through this class.
 *
 * @author Sebastian Riedel
 */
public final class QueryFactory {

    /**
     * The singleton query factory.
     */
    private static QueryFactory instance = new QueryFactory();

    /**
     * Private constructor to create singleton instance.
     */
    private QueryFactory() {

    }

    /**
     * This method returns the singleton query factory.
     *
     * @return the single query factory available in a JVM.
     */
    public static QueryFactory getInstance() {
        return instance;
    }

    /**
     * Create a new atom.
     *
     * @param pred the predicate of the atom.
     * @param args the argument terms of the atom.
     * @return an atom for the given predicate and arguments.
     */
    public Atom createAtom(final Predicate pred, final List<Term> args) {
        return new Atom(pred, args);
    }

    /**
     * Convenience method that creates atoms for arguments can be terms or normal java objects. In case non-term objects
     * are used they are naturally mapped to their corresponding constant with respect to the argument types of the
     * predicate.
     * <p/>
     * Note that lowercase strings are mapped to Variables.
     *
     * @param pred the predicate of the atom.
     * @param args the argument sequence of objects.
     * @return an atom with the given predicate and arguments.
     */
    public Atom createAtom(final Predicate pred, final Object... args) {
        List<Term> argTerms = WorldUtils.resolveArguments(pred, args);
        return createAtom(pred, argTerms);
    }

    


    /**
     * Creates a new clause with the given head and body atoms.
     *
     * @param inner the inner conjunction of the query.
     * @param outer the outer conjunction of the query.
     * @return a Query with the given inner and outer conjunction.
     */
    public Query createQuery(final List<Atom> inner,
                             final List<Atom> outer) {
        return new Query(inner, outer);
    }

    /**
     * Returns a builder for easy construction of queries that uses the singleton factory.
     *
     * @return a QueryBuilder that uses global clause factory
     */
    public static QueryBuilder build() {
        return new QueryBuilder(instance);
    }


}
