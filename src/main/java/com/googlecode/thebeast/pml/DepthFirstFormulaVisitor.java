package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Term;
import com.googlecode.thebeast.query.TermVisitor;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Constant;

/**
 * A DepthFirstFormulaVisitor visits the nodes of a formula tree in a depth first fashion and calls the "in" method of a
 * subclass before a node is visited, and the "out" method after the node was visited.
 *
 * @author Sebastian Riedel
 */
public abstract class DepthFirstFormulaVisitor implements FormulaVisitor, TermVisitor {

    protected void inBinaryOperatorFormula(BinaryOperatorFormula binaryOperatorFormula) {}

    protected void outBinaryOperatorFormula(BinaryOperatorFormula binaryOperatorFormula) {}

    public void visitBinaryOperatorFormula(BinaryOperatorFormula binaryOperatorFormula) {
        inBinaryOperatorFormula(binaryOperatorFormula);
        binaryOperatorFormula.getArg1().accept(this);
        binaryOperatorFormula.getArg2().accept(this);
        outBinaryOperatorFormula(binaryOperatorFormula);
    }

    protected void inNegatedFormula(NegatedFormula negatedFormula){}

    protected void outNegatedFormula(NegatedFormula negatedFormula){}

    public void visitNegatedFormula(NegatedFormula negatedFormula) {
        inNegatedFormula(negatedFormula);
        negatedFormula.getArgument().accept(this);
        outNegatedFormula(negatedFormula);
    }

    protected void inAtomFormula(AtomFormula atomFormula) {}
    protected void outAtomFormula(AtomFormula atomFormula) {}

    public void visitAtomFormula(AtomFormula atomFormula) {
        inAtomFormula(atomFormula);
        for (Term term : atomFormula.getArguments())
            term.accept(this);
        outAtomFormula(atomFormula);
    }

    public void visitConstant(Constant constant) {

    }

    public void visitVariable(Variable variable) {

    }
}
