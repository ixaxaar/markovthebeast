package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Term;
import com.googlecode.thebeast.world.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public final class AtomFormula extends ComposableFormula {

    private final Predicate predicate;
    private final List<Term> arguments;

    public AtomFormula(Predicate predicate, List<? extends Term> arguments) {
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

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(predicate.getName());
        result.append("(");
        if (arguments.size() > 0){
            result.append(arguments.get(0));
            for (int i = 1; i < arguments.size(); ++i)
                result.append(",").append(arguments.get(i));
        }
        result.append(")");
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtomFormula that = (AtomFormula) o;

        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        if (predicate != null ? !predicate.equals(that.predicate) : that.predicate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = predicate != null ? predicate.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }
}
