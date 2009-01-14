package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Symbol;
import com.googlecode.thebeast.world.Type;

/**
 * @author Sebastian Riedel
 */
public class TypeMismatchException extends RuntimeException {
    private Type expectedType;
    private Type actualType;
    private Term actualTerm;
    private Symbol context;

    public TypeMismatchException(Type expectedType, Type actualType, Term actualTerm, Symbol context) {
        super("Excepted the type " + expectedType + " but got " + actualType + " of " + actualTerm
            + " for " + context);
        this.expectedType = expectedType;
        this.actualType = actualType;
        this.actualTerm = actualTerm;
        this.context = context;
    }

    public Type getExpectedType() {
        return expectedType;
    }

    public Type getActualType() {
        return actualType;
    }

    public Term getActualTerm() {
        return actualTerm;
    }

    public Symbol getContext() {
        return context;
    }
}
