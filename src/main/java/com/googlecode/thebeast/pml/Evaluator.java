package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public class Evaluator extends DepthFirstFormulaVisitor {

    private double result = 0;
    private GroundAtomAssignment assignment;

    public double evaluate(final Formula formula, final GroundAtomAssignment assignment){
        this.assignment = assignment;
        formula.accept(this);
        return result;
    }

    @Override
    protected void outAtomFormula(AtomFormula atomFormula) {
        result = assignment.getValue(atomFormula) ? 1.0 : 0.0;
    }

    @Override
    public void visitBinaryOperatorFormula(BinaryOperatorFormula binaryOperatorFormula) {
        binaryOperatorFormula.getArg1().accept(this);
        double arg1 = result;
        binaryOperatorFormula.getArg2().accept(this);
        double arg2 = result;
        result = binaryOperatorFormula.getOperator().evaluate(arg1, arg2);
    }

    @Override
    protected void outNegatedFormula(NegatedFormula negatedFormula) {
        result = 1.0 - result;
    }

}
