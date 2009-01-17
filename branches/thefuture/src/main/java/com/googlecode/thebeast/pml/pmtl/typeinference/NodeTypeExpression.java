package com.googlecode.thebeast.pml.pmtl.typeinference;

/**
 * @author Sebastian Riedel
*/
public interface NodeTypeExpression {
    void accept(NodeTypeExpressionVisitor visitor);
}
