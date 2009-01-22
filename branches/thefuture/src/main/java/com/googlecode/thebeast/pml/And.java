package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public final class And extends BinaryOperator {

    public final static And AND = new And();

    public And(){
        super("and", "^");
    }

    public double evaluate(final double arg1, final double arg2) {
        return Math.min(arg1,arg2);
    }

    @Override
    public void accept(final BinaryOperatorVisitor visitor) {
        visitor.visitAnd(this);
    }
}
