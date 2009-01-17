package com.googlecode.thebeast.pml.pmtl.typeinference;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A NodeTypeList is a NodeTypeExpression that describes the type of a predicate. Here the type expression in the list
 * at index i corresponds to the type of the argument of the predicate at index i.
 *
 * @author Sebastian Riedel
 */
public class NodeTypeList extends ArrayList<NodeTypeExpression> implements NodeTypeExpression {

    /**
     * Creates an new NodeTypeList with the given type expressions.
     *
     * @param types the argument type expressions.
     */
    public NodeTypeList(NodeTypeExpression... types) {
        addAll(Arrays.asList(types));
    }

    /**
     * Tells the visitor to visit this object as a NodeTypeList.
     *
     * @param visitor the visitor to accept.
     */
    public void accept(NodeTypeExpressionVisitor visitor) {
        visitor.visitNodeTypeList(this);
    }

    
}
