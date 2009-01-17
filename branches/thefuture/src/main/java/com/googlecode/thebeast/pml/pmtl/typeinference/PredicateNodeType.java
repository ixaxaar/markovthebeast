package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.world.Type;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Sebastian Riedel
*/
public class PredicateNodeType extends ArrayList<Type> implements NodeType {


    public PredicateNodeType() {
    }

    public PredicateNodeType(Collection<? extends Type> c) {
        super(c);
    }

    public void accept(NodeTypeExpressionVisitor visitor) {
        visitor.visitNodeType(this);
    }
}
