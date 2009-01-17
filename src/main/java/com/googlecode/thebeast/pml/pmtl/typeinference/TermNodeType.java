package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.world.Type;

/**
 * @author Sebastian Riedel
*/
public class TermNodeType implements NodeType {
    private Type type;

    public TermNodeType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void accept(NodeTypeExpressionVisitor visitor) {
        visitor.visitNodeType(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermNodeType that = (TermNodeType) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
