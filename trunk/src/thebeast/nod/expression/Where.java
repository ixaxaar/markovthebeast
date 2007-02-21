package thebeast.nod.expression;

import thebeast.nod.type.BoolType;
import thebeast.nod.type.RelationType;

/**
 * @author Sebastian Riedel
 */
public interface Where extends Expression<RelationType> {
    Expression<BoolType> condition();
    Expression<RelationType> relation();
}
