package com.googlecode.thebeast.pml.pmtl.typeinference;

/**
 * @author Sebastian Riedel
*/
public class NodeTypeEquation {
    NodeTypeExpression left, right;

    public NodeTypeEquation(NodeTypeExpression left, NodeTypeExpression right) {
        this.left = left;
        this.right = right;
    }

    void substitute(NodeTypeSubstitution nodeTypeSubstitution) {
        this.left = nodeTypeSubstitution.substitute(left);
        this.right = nodeTypeSubstitution.substitute(right);
    }
}
