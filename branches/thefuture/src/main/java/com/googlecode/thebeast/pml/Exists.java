package com.googlecode.thebeast.pml;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public final class Exists implements FirstOrderOperator {

    public final static Exists EXISTS = new Exists();

    public double evaluate(Collection<GroundNode> nodes, Assignment assignment) {
        for (GroundNode node : nodes)
            if (assignment.getValue(node) == 1.0) return 1.0;
        return 0.0;
    }
}
