package com.googlecode.thebeast.pml.pmtl.typeinference;

/**
 * @author Sebastian Riedel
*/
public interface NodeTypeExpressionVisitor {
    void visitNodeTypeVariable(NodeTypeVariable nodeTypeVariable);

    void visitNodeTypeList(NodeTypeList nodeTypeList);

    void visitNodeType(NodeType nodeType);
}
