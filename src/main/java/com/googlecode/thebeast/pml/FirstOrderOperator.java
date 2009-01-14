package com.googlecode.thebeast.pml;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public interface FirstOrderOperator {

    double evaluate(Collection<GroundNode> nodes, Assignment assignment);

}
