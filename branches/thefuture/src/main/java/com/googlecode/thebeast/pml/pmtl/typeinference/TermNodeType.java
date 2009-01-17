package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.world.Type;

/**
 * A TermNodeType represents the type of a term node. This class is essentially a wrapper around the {@link Type} class
 * in order to avoid having it to implement the NodeType interface (and hence removing a dependency from the type
 * inference package to the toplevel world package.
 *
 * @author Sebastian Riedel
 */
public class TermNodeType implements NodeType {
    private Type type;

    /**
     * Creates a new TermNodeType with the given type.
     *
     * @param type the type of a term.
     */
    public TermNodeType(Type type) {
        this.type = type;
    }

    /**
     * Returns the type of a term.
     *
     * @return the type of a term.
     */
    public Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public void accept(NodeTypeExpressionVisitor visitor) {
        visitor.visitNodeType(this);
    }

    /**
     * Returns whether the given parameter equals this TermNodeType object.
     *
     * @param o the object to compare to.
     * @return true iff the other object is a TermNodeType containing the same type as this TermNodeType.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermNodeType that = (TermNodeType) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    /**
     * Returns a hashcode based on the contained type of this TermNodeType.
     *
     * @return a hashcode based on the contained type of this TermNodeType.
     */
    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    /**
     * Simply returns the string representation of the contained type.
     *
     * @return the string representation of the contained type.
     */
    @Override
    public String toString() {
        return type.toString();
    }
}
