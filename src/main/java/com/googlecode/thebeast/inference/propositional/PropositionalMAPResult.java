package com.googlecode.thebeast.inference.propositional;

import com.googlecode.thebeast.pml.GroundAtomAssignment;

/**
 * @author Sebastian Riedel
 */
public class PropositionalMAPResult {

    private GroundAtomAssignment assignment;


    public PropositionalMAPResult(GroundAtomAssignment assignment) {
        this.assignment = assignment;
    }

    public GroundAtomAssignment getAssignment() {
        return assignment;
    }
}
