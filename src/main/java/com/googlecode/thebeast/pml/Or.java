package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public final class Or extends BinaryOperator {

    public Or(){
        super("or", "v");
    }

    public double evaluate(final double arg1, final double arg2) {
        return Math.max(arg1,arg2);
    }

    @Override
    public void accept(final BinaryOperatorVisitor visitor) {
        visitor.visitOr(this);
    }
}