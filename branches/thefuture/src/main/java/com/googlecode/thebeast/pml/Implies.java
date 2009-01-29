package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public final class Implies extends BinaryOperator {


    public final static Implies IMPLIES = new Implies();

    public Implies(){
        super("implies", "=>");
    }

    public double evaluate(final double arg1, final double arg2) {
        return Math.max(1.0 - arg1,arg2);
    }

    @Override
    public void accept(final BinaryOperatorVisitor visitor) {
        visitor.visitImplies(this);
    }
}