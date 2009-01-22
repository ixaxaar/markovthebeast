package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public final class BinaryOperatorFormula extends ComposableFormula  {

    private final BinaryOperator operator;

    private final ComposableFormula arg1;
    private final ComposableFormula arg2;

    public BinaryOperatorFormula(BinaryOperator operator, ComposableFormula arg1, ComposableFormula arg2) {
        this.operator = operator;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    public ComposableFormula getArg1() {
        return arg1;
    }

    public ComposableFormula getArg2() {
        return arg2;
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visitBinaryOperatorFormula(this);
    }
}
