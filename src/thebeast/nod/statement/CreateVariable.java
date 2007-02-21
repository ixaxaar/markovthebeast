package thebeast.nod.statement;

import thebeast.nod.identifier.Name;
import thebeast.nod.expression.Expression;

/**
 * @author Sebastian Riedel
 */
public interface CreateVariable extends Statement {
    Name identifier();
    Expression initialization();
}
