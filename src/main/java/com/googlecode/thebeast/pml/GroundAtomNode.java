package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Constant;
import com.googlecode.thebeast.query.Term;

/**
 * @author Sebastian Riedel
 */
public class GroundAtomNode {

    private final Predicate predicate;
    private final Tuple arguments;

    public GroundAtomNode(Predicate predicate, Tuple arguments) {
        this.predicate = predicate;
        this.arguments = arguments;
    }

    public GroundAtomNode(AtomFormula atom) {
        this.predicate = atom.getPredicate();
        arguments = Tuple.createFromGroundTerms(atom.getArguments());
        
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Tuple getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroundAtomNode that = (GroundAtomNode) o;

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

    @Override
    public String toString() {
        return new AtomFormula(predicate, arguments).toString();
    }
}
