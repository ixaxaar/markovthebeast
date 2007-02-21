package thebeast.nod.statement;

import thebeast.nod.variable.Scope;
import thebeast.nod.identifier.Name;
import thebeast.nod.expression.Expression;

/**
 * @author Sebastian Riedel
 */
public interface CreateVariable extends Statement {
    Scope database();
    Name identifier();
    Expression initialization();
}
