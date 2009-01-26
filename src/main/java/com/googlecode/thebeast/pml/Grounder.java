package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.Term;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Constant;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class Grounder extends FormulaCopier {

    private NestedSubstitution substitution;
    private ArrayList<Term> terms = new ArrayList<Term>();

    public Formula ground(Formula original, NestedSubstitution substitution){
        this.substitution = substitution;
        original.accept(this);
        return getCopyResult();
    }

    @Override
    protected void inAtomFormula(AtomFormula atomFormula) {
        terms.clear();
    }

    @Override
    protected void outAtomFormula(AtomFormula atomFormula) {
        setCopyFormula(new AtomFormula(atomFormula.getPredicate(), terms));
    }

    @Override
    public void visitConstant(Constant constant) {
        terms.add(constant);
    }

    @Override
    public void visitVariable(Variable variable) {
        //todo check whether variable is bound!
        terms.add(substitution.getOuterSubstitution().get(variable));
    }


}
