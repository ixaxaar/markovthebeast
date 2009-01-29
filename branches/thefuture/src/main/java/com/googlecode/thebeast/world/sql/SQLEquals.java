package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An SQL implementation of a static equals predicate.
 *
 * @author Sebastian Riedel
 */
final class SQLEquals extends SQLStaticPredicate {

    private final List<SQLRepresentableType> argTypes;

    SQLEquals(final SQLRepresentableType type, final SQLSignature signature) {
        super(type.getName().toLowerCase() + "Equals", signature);
        this.argTypes = Collections.unmodifiableList(Arrays.asList(type, type));
    }

    public boolean evaluate(final Tuple arguments) {
        return arguments.get(0).equals(arguments.get(1));
    }

    public List<? extends Type> getArgumentTypes() {
        return argTypes;
    }

}
