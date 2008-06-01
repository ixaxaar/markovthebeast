package thebeast.nod.statement;

import thebeast.nod.expression.Expression;
import thebeast.nod.expression.VariableReference;
import thebeast.nod.variable.Variable;

/**
 * @author Sebastian Riedel
 */
public interface Assign extends Statement {
    Variable target();
    Expression expression();

}
