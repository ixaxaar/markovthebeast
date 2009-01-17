package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.world.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * A PredicateNodeType describes the type of a predicate (node) as a list of argument types.
 *
 * @author Sebastian Riedel
 */
public class PredicateNodeType extends ArrayList<Type> implements NodeType {

    /**
     * Creates a 0-ary predicate node type.
     */
    public PredicateNodeType() {
    }

    /**
     * Creates a predicate node type containing the sequence of node types of the given parameter.
     *
     * @param c the sequence of argument types of this predicate type.
     */
    public PredicateNodeType(List<? extends Type> c) {
        super(c);
    }

    /**
     * Accepts NodeTypeExpressionVisitor and tells them to visit this object as a NodeType.
     *
     * @param visitor the visitor to accept.
     */
    public void accept(NodeTypeExpressionVisitor visitor) {
        visitor.visitNodeType(this);
    }

   
}
