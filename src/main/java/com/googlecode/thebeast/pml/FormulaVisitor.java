package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public interface FormulaVisitor {
    void visitAtomFormula(AtomFormula atomFormula);

    void visitBinaryOperatorFormula(BinaryOperatorFormula binaryOperatorFormula);

    void visitNegatedFormula(NegatedFormula negatedFormula);
}
