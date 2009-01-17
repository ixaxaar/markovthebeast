package com.googlecode.thebeast.pml.pmtl.typeinference;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Sebastian Riedel
*/
public class NodeTypeList extends ArrayList<NodeTypeExpression> implements NodeTypeExpression {

    public NodeTypeList(NodeTypeExpression ... types){
        addAll(Arrays.asList(types));
    }

    

    public void accept(NodeTypeExpressionVisitor visitor) {
        visitor.visitNodeTypeList(this);
    }
}
