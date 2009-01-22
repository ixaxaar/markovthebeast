package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.query.Term;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Sebastian Riedel
 */
public final class AtomFormula extends ComposableFormula {

    private final Predicate predicate;
    private final List<Term> arguments;

    public AtomFormula(Predicate predicate, List<Term> arguments) {
        this.predicate = predicate;
        this.arguments = new ArrayList<Term>(arguments);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<Term> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visitAtomFormula(this);
    }
}
