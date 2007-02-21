package thebeast.nod.util;

import thebeast.nod.NoDServer;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.identifier.Name;
import thebeast.nod.expression.Expression;
import thebeast.nod.expression.VariableReference;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Statement;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.type.RelationType;

import java.util.Stack;

/**
 * @author Sebastian Riedel
 */
public class StatementBuilder {

    private NoDServer server;
    private StatementFactory statementFactory;
    private Stack<Statement> statements = new Stack<Statement>();

    public StatementBuilder(NoDServer server) {
        this.server = server;
        statementFactory = server.statementFactory();
    }

    public StatementBuilder insert(RelationVariable ref, RelationExpression expr){
        statements.push(statementFactory.createInsert(ref,expr));
        return this;
    }


    public Statement result(){
        return statements.peek();
    }

    
}
