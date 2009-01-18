package com.googlecode.thebeast.pml.pmtl.typeinference;

/**
 * A NodeTypeExpressionVisitor visits NodeTypeExpression objects which call the visit method corresponding to their type
 * on the visitor.
 *
 * @author Sebastian Riedel
 */
public interface NodeTypeExpressionVisitor {

    /**
     * Called if a NodeTypeVariable is to be visited.
     *
     * @param nodeTypeVariable the nodeTypeVariable to visit.
     */
    void visitNodeTypeVariable(NodeTypeVariable nodeTypeVariable);

    /**
     * Called if a list of node type expressions (i.e. the type of a predicate) is to be visited.
     *
     * @param nodeTypeList the list of types to visit.
     */
    void visitNodeTypeList(NodeTypeList nodeTypeList);

    /**
     * Called if an actual node type is to be visited.
     *
     * @param nodeType the node type to visit.
     */
    void visitNodeType(NodeType nodeType);
}
