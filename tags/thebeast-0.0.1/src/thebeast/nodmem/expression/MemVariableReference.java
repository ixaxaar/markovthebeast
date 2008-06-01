package thebeast.nodmem.expression;

import thebeast.nod.identifier.Identifier;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.VariableReference;
import thebeast.nod.type.Type;
import thebeast.nodmem.expression.AbstractMemExpression;

/**
 * @author Sebastian Riedel
 */
public class MemVariableReference<T extends Type> extends AbstractMemExpression<T> implements VariableReference<T> {

    private Identifier name;

    public MemVariableReference(T type, Identifier name) {
        super(type);
        this.name = name;
    }


    public Identifier identifier() {
        return name;
    }

    public void acceptExpressionVisitor(ExpressionVisitor visitor) {
        //visitor.visitRelationVariableReference(this);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemVariableReference that = (MemVariableReference) o;

        return name.equals(that.name) && type.equals(that.type);

    }

    public int hashCode() {
        int result;
        result = type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
