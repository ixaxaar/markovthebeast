package com.googlecode.thebeast.pml.pmtl.typeinference;

import java.util.List;
import java.util.Stack;

/**
 * A NodeTypeExpressionUnifier takes a list of node type equations and finds a node type substitution that satisfies all
 * these equations.
 *
 * @author Sebastian Riedel
 */
public class NodeTypeExpressionUnifier {

    /**
     * This method takes a list of node type equations and finds a node type substitution that satisfies all equations.
     *
     * @param equations the equations to satisfy.
     * @return the substitution that satisfies the equation.
     */
    public static NodeTypeSubstitution unify(List<NodeTypeEquation> equations) {
        NodeTypeSubstitution result = new NodeTypeSubstitution();
        Stack<NodeTypeEquation> equationStack = new Stack<NodeTypeEquation>();
        equationStack.addAll(equations);

        while (equationStack.size() > 0) {
            NodeTypeEquation equation = equationStack.pop();
            if (!equation.getLeft().equals(equation.getRight())) {
                if (equation.getLeft() instanceof NodeTypeVariable) {
                    NodeTypeSubstitution newSubstitution =
                        new NodeTypeSubstitution((NodeTypeVariable) equation.getLeft(), equation.getRight());
                    result.substituteAll(newSubstitution);
                    result.putAll(newSubstitution);
                    for (NodeTypeEquation eq : equationStack)
                        eq.substitute(newSubstitution);

                } else if (equation.getRight() instanceof NodeTypeVariable) {
                    NodeTypeSubstitution newSubstitution =
                        new NodeTypeSubstitution((NodeTypeVariable) equation.getRight(), equation.getLeft());
                    result.substituteAll(newSubstitution);
                    result.putAll(newSubstitution);
                    for (NodeTypeEquation eq : equationStack)
                        eq.substitute(newSubstitution);

                } else {
                    throw new RuntimeException("Type expressions " + equation.getLeft()
                        + " and " + equation.getRight() + "cannot be unified");
                }
            }
        }
        return result;
    }

}
