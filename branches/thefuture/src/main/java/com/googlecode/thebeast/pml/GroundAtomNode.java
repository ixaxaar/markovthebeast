package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public class GroundAtomNode {

    private final AtomFormula atom;

    public GroundAtomNode(AtomFormula atom) {
        this.atom = atom;
    }

    public AtomFormula getAtom() {
        return atom;
    }
}
