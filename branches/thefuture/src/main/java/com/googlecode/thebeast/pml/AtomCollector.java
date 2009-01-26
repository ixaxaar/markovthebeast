package com.googlecode.thebeast.pml;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class AtomCollector extends DepthFirstFormulaVisitor {

    private ArrayList<AtomFormula> atoms;

    public List<AtomFormula> collect(Formula formula){
        atoms = new ArrayList<AtomFormula>();
        formula.accept(this);
        return atoms;
    }

    @Override
    protected void outAtomFormula(AtomFormula atomFormula) {
        atoms.add(atomFormula);
    }
}
