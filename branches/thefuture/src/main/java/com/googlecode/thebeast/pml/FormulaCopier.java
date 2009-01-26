package com.googlecode.thebeast.pml;

import java.util.Stack;

/**
 * @author Sebastian Riedel
 */
public class FormulaCopier extends DepthFirstFormulaVisitor {

    private Stack<Formula> formulas = new Stack<Formula>();

    @Override                   
    protected void outBinaryOperatorFormula(BinaryOperatorFormula binaryOperatorFormula) {
        Formula arg2 = formulas.pop();
        Formula arg1 = formulas.pop();
        formulas.push(new BinaryOperatorFormula(binaryOperatorFormula.getOperator(),
            (ComposableFormula) arg1, (ComposableFormula) arg2));
        super.outBinaryOperatorFormula(binaryOperatorFormula);
    }

    @Override
    public void outNegatedFormula(NegatedFormula negatedFormula) {
        formulas.push(new NegatedFormula((ComposableFormula) formulas.pop()));
    }

    @Override
    protected void outAtomFormula(AtomFormula atomFormula) {
        formulas.push(new AtomFormula(atomFormula.getPredicate(), atomFormula.getArguments()));
    }

    protected void setCopyFormula(Formula formula){
        formulas.push(formula);
    }

    public Formula getCopyResult(){
        return formulas.peek();
    }

}
