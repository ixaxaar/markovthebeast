package thebeast.nodmem.statement;

import thebeast.nod.statement.Assign;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.expression.Expression;
import thebeast.nod.variable.Variable;
import thebeast.nodmem.variable.AbstractMemVariable;

/**
 * @author Sebastian Riedel
 */
public class MemAssign implements Assign {

    private AbstractMemVariable target;
    private Expression expression;

    public MemAssign(AbstractMemVariable target, Expression expression) {
        this.target = target;
        this.expression = expression;
    }

    public Variable target() {
        return target;
    }

    public Expression expression() {
        return expression;
    }

    public void acceptStatementVisitor(StatementVisitor visitor) {
        visitor.visitAssign(this);
    }
}
