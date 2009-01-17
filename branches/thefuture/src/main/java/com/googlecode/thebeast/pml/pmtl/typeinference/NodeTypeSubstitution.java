package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.pml.pmtl.node.PTerm;
import com.googlecode.thebeast.world.Type;

import java.util.LinkedHashMap;
import java.util.Stack;

/**
 * @author Sebastian Riedel
*/
public class NodeTypeSubstitution extends LinkedHashMap<NodeTypeVariable, NodeTypeExpression>
    implements NodeTypeExpressionVisitor {

    Stack<NodeTypeExpression> stack = new Stack<NodeTypeExpression>();

    public NodeTypeSubstitution() {

    }

    public Type getType(PTerm term){
        return ((TermNodeType)get(new NodeTypeVariable(term))).getType();
    }

    public NodeTypeSubstitution(NodeTypeVariable var, NodeTypeExpression expr) {
        put(var, expr);
    }

    void substituteAll(NodeTypeSubstitution substitution) {
        for (NodeTypeVariable var : keySet()) {
            put(var, substitution.substitute(get(var)));
        }
    }

    synchronized NodeTypeExpression substitute(NodeTypeExpression expression) {
        expression.accept(this);
        return stack.pop();
    }

    public void visitNodeTypeVariable(NodeTypeVariable nodeTypeVariable) {
        NodeTypeExpression mappedType = get(nodeTypeVariable);
        stack.push(mappedType == null ? nodeTypeVariable : mappedType);
    }

    public void visitNodeTypeList(NodeTypeList nodeTypeList) {
        NodeTypeList result = new NodeTypeList();
        boolean allArgsAreTermNodeTypes = true;
        for (NodeTypeExpression expr : nodeTypeList) {
            expr.accept(this);
            allArgsAreTermNodeTypes &= expr instanceof TermNodeType;
            result.add(stack.pop());
        }
        if (allArgsAreTermNodeTypes) {
            PredicateNodeType predicateNodeType = new PredicateNodeType();
            for (NodeTypeExpression expr : result)
                predicateNodeType.add(((TermNodeType) expr).getType());
            stack.push(predicateNodeType);
        } else
            stack.push(result);
    }

    public void visitNodeType(NodeType nodeType) {
        stack.push(nodeType);
    }
}
