package com.googlecode.thebeast.pml;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public final class GroundAtomAssignment {
    public GroundAtomAssignment(GroundAtomAssignment original) {
        
    }

    public boolean getValue(AtomFormula atom){
        return false;
    }

    public boolean hasValue(AtomFormula atom){
        return false;
    }

    public void setValue(AtomFormula atom, boolean value){

    }

    public void setValue(Collection<AtomFormula> atoms, boolean value){
        for (AtomFormula atom : atoms)
            setValue(atom, value);
    }

}
