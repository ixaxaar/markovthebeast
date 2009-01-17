package com.googlecode.thebeast.pml.pmtl.typeinference;

/**
 * A NodeTypeExpression is an expression that describes the type of a node.
 *
 * @author Sebastian Riedel
 */
public interface NodeTypeExpression {
    /**
     * Accepts a NodeTypeExpressionVisitor and call the visit method of the visitor that matches this expression's
     * type.
     *
     * @param visitor the visitor to accept.
     */
    void accept(NodeTypeExpressionVisitor visitor);
}
