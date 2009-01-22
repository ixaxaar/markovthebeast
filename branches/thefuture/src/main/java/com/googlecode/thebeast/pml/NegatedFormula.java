package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public final class NegatedFormula extends ComposableFormula {

    private final ComposableFormula argument;

    public NegatedFormula(ComposableFormula argument) {
        this.argument = argument;
    }

    public ComposableFormula getArgument() {
        return argument;
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visitNegatedFormula(this);
    }
}
