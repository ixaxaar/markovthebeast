package thebeast.nodmem;

import thebeast.nod.identifier.Name;
import thebeast.nod.identifier.IdentifierFactory;
import thebeast.nod.NoDServer;
import thebeast.nod.expression.ExpressionFactory;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.variable.*;
import thebeast.nodmem.expression.MemExpressionFactory;
import thebeast.nodmem.type.MemTypeFactory;
import thebeast.nodmem.variable.*;
import thebeast.nodmem.identifier.MemName;
import thebeast.nodmem.identifier.MemIdentifierFactory;
import thebeast.nodmem.statement.MemStatementFactory;
import thebeast.nodmem.statement.MemInterpreter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemNoDServer implements NoDServer {

  private MemTypeFactory typeFactory = new MemTypeFactory();
  private VariableFactory variableFactory = new MemVariableFactory();
  private Scope root = new MemScope();
  private LinkedList<Scope> scopes = new LinkedList<Scope>();
  private MemExpressionFactory expressionFactory = new MemExpressionFactory();
  private StatementFactory statementFactory = new MemStatementFactory();
  private IdentifierFactory identifierFactory = new MemIdentifierFactory();
  private MemInterpreter memInterpreter = new MemInterpreter(this);

  public Scope getRootScope() {
    return root;
  }

  public Scope createScope() {
    MemScope memScope = new MemScope();
    scopes.add(memScope);
    return memScope;
  }

  public List<? extends Scope> anonymousScopes() {
    return scopes;
  }

  public VariableFactory variableFactory() {
    return variableFactory;
  }

  public TypeFactory typeFactory() {
    return typeFactory;
  }

  public StatementFactory statementFactory() {
    return statementFactory;
  }

  public ExpressionFactory expressionFactory() {
    return expressionFactory;
  }

  public IdentifierFactory identifierFactory() {
    return identifierFactory;
  }

  public Interpreter interpreter() {
    return memInterpreter;
  }


  public Name createIdentifier(String identifier) {
    return new MemName(identifier);
  }


}
