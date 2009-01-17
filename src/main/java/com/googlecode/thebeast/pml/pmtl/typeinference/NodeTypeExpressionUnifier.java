package com.googlecode.thebeast.pml.pmtl.typeinference;

import java.util.List;
import java.util.Stack;

/**
 * @author Sebastian Riedel
 */
public class NodeTypeExpressionUnifier {

    public static NodeTypeSubstitution unify(List<NodeTypeEquation> equations) {
        NodeTypeSubstitution result = new NodeTypeSubstitution();
        Stack<NodeTypeEquation> equationStack = new Stack<NodeTypeEquation>();
        equationStack.addAll(equations);

        while (equationStack.size() > 0) {
            NodeTypeEquation equation = equationStack.pop();
            if (!equation.left.equals(equation.right)) {
                if (equation.left instanceof NodeTypeVariable) {
                    NodeTypeSubstitution newSubstitution =
                        new NodeTypeSubstitution((NodeTypeVariable) equation.left, equation.right);
                    result.substituteAll(newSubstitution);
                    result.putAll(newSubstitution);
                    for (NodeTypeEquation eq : equationStack)
                        eq.substitute(newSubstitution);

                } else if (equation.right instanceof NodeTypeVariable) {
                    NodeTypeSubstitution newSubstitution =
                        new NodeTypeSubstitution((NodeTypeVariable) equation.right, equation.left);
                    result.substituteAll(newSubstitution);
                    result.putAll(newSubstitution);
                    for (NodeTypeEquation eq : equationStack)
                        eq.substitute(newSubstitution);

                } else {
                    throw new RuntimeException("Type expressions " + equation.left
                        + " and " + equation.right + "cannot be unified");
                }
            }
        }
        return result;
    }

}
