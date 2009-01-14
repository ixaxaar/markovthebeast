package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.AbstractSymbol;
import com.googlecode.thebeast.world.StaticPredicate;

/**
 * @author Sebastian Riedel
 */
abstract class SQLStaticPredicate extends AbstractSymbol
    implements StaticPredicate {

    /**
     * Creates an SQLStaticPredicate with the given name and signature.
     *
     * @param name      the name of the predicate.
     * @param signature the signature of the predicate.
     */
    protected SQLStaticPredicate(final String name, final SQLSignature signature) {
        super(name, signature);
    }

    /**
     * Returns true because any StaticPredicate inheritor must be static.
     *
     * @return true.
     */
    public boolean isStatic() {
        return true;
    }
}
