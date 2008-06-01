package thebeast.nod.statement;

/**
 * @author Sebastian Riedel
 */
public interface Statement {
    void acceptStatementVisitor(StatementVisitor visitor);
}
