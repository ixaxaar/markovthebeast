package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public abstract class BinaryOperator {

    private final String name;
    private final String infix;

    public String getName() {
        return name;
    }

    public String getInfix() {
        return infix;
    }

    protected BinaryOperator(final String name, final String infix) {
        this.name = name;
        this.infix = infix;
    }

    public abstract double evaluate(double arg1, double arg2);

    public abstract void accept(BinaryOperatorVisitor visitor);

}
