package thebeast.nodmem;

import thebeast.nod.NoDServer;
import thebeast.nod.Dump;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.expression.ExpressionFactory;
import thebeast.nod.identifier.IdentifierFactory;
import thebeast.nod.identifier.Name;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.variable.VariableFactory;
import thebeast.nodmem.expression.MemExpressionFactory;
import thebeast.nodmem.identifier.MemIdentifierFactory;
import thebeast.nodmem.identifier.MemName;
import thebeast.nodmem.statement.MemInterpreter;
import thebeast.nodmem.statement.MemStatementFactory;
import thebeast.nodmem.type.MemTypeFactory;
import thebeast.nodmem.variable.MemVariableFactory;

/**
 * @author Sebastian Riedel
 */
public class MemNoDServer implements NoDServer {

  private MemTypeFactory typeFactory = new MemTypeFactory();
  private VariableFactory variableFactory = new MemVariableFactory();
  private MemExpressionFactory expressionFactory = new MemExpressionFactory();
  private StatementFactory statementFactory = new MemStatementFactory();
  private IdentifierFactory identifierFactory = new MemIdentifierFactory();
  private MemInterpreter memInterpreter = new MemInterpreter(this);
  private ExpressionBuilder expressionBuilder = new ExpressionBuilder(this);


  public TypeFactory typeFactory() {
    return typeFactory;
  }

  public StatementFactory statementFactory() {
    return statementFactory;
  }

  public ExpressionFactory expressionFactory() {
    return expressionFactory;
  }

  public ExpressionBuilder expressionBuilder() {
    return expressionBuilder;
  }

  public IdentifierFactory identifierFactory() {
    return identifierFactory;
  }

  public Interpreter interpreter() {
    return memInterpreter;
  }

  public Dump createDump(String filename, boolean createNew, int bufferSize) {
    return new MemDump(filename, createNew, bufferSize);
  }


  public Name createIdentifier(String identifier) {
    return new MemName(identifier);
  }


}
