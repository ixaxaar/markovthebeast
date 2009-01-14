package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Substitution;

/**
 * @author Sebastian Riedel
 */
public class FeatureIndex {

    private Substitution substitution;

    public FeatureIndex(Substitution substitution) {
        this.substitution = substitution;
    }

    public Substitution getSubstitution() {
        return substitution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeatureIndex that = (FeatureIndex) o;

        if (substitution != null ? !substitution.equals(that.substitution) : that.substitution != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return substitution != null ? substitution.hashCode() : 0;
    }
}
