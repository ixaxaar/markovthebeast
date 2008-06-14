package thebeast.nod.expression;

import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public interface Expression<T extends Type> {
    T type();

    void acceptExpressionVisitor(ExpressionVisitor visitor);

}
