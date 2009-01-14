package com.googlecode.thebeast.inference.propositional;

import com.googlecode.thebeast.pml.Assignment;

/**
 * @author Sebastian Riedel
 */
public class PropositionalMAPResult {

    private Assignment assignment;

    public PropositionalMAPResult(Assignment assignment) {
        this.assignment = assignment;
    }

    public Assignment getAssignment() {
        return assignment;
    }
}
