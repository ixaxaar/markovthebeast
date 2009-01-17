package com.googlecode.thebeast.pml.pmtl.typeinference;

/**
 * A NodeTypeEquation equates two NodeTypeExpression objects, a left and and a right expression.
 *
 * @author Sebastian Riedel
 */
public class NodeTypeEquation {

    private NodeTypeExpression left, right;

    /**
     * Creates a new NodeTypeEquation with the given parameters.
     *
     * @param left  the left expression.
     * @param right the right expression.
     */
    public NodeTypeEquation(NodeTypeExpression left, NodeTypeExpression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Applies the given substitution to the left and right expressions and replaces them by the result of these
     * applications.
     *
     * @param nodeTypeSubstitution the substitution to apply to the left and right expressions.
     */
    void substitute(final NodeTypeSubstitution nodeTypeSubstitution) {
        this.left = nodeTypeSubstitution.substitute(left);
        this.right = nodeTypeSubstitution.substitute(right);
    }

    /**
     * Returns the left expression.
     *
     * @return the left expression.
     */
    public NodeTypeExpression getLeft() {
        return left;
    }

    /**
     * Returns the right expression.
     *
     * @return the right expression.
     */
    public NodeTypeExpression getRight() {
        return right;
    }

    /**
     * Returns a simple string representation of this equation.
     *
     * @return a string representation of this equation.
     */
    @Override
    public String toString() {
        return left + " = " + right;
    }

    /**
     * Returns true if left and right equation of this and that equation are equal, respectively.
     *
     * @param o the other equation.
     * @return true iff left and right equation of both equations are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeTypeEquation that = (NodeTypeEquation) o;

        if (left != null ? !left.equals(that.left) : that.left != null) return false;
        if (right != null ? !right.equals(that.right) : that.right != null) return false;

        return true;
    }

    /**
     * Returns hashcode based on left and right expression.
     *
     * @return hashcode based on left and right expression.
     */
    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
