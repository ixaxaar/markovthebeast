package thebeast.nodmem;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.NoDServer;
import thebeast.nod.expression.ExpressionFactory;
import thebeast.nod.identifier.IdentifierFactory;
import thebeast.nod.identifier.Name;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nodmem.expression.MemExpressionFactory;
import thebeast.nodmem.identifier.MemIdentifierFactory;
import thebeast.nodmem.identifier.MemName;
import thebeast.nodmem.statement.MemInterpreter;
import thebeast.nodmem.statement.MemStatementFactory;
import thebeast.nodmem.type.MemTypeFactory;

import java.io.File;

/**
 * @author Sebastian Riedel
 */
public class MemNoDServer implements NoDServer {

  private MemTypeFactory typeFactory = new MemTypeFactory();
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

  public FileSink createSink(File file, int bufferSizeInKb) {
    return new MemFileSink(file, bufferSizeInKb);
  }

  public FileSource createSource(File file, int bufferSizeInKb) {
    return new MemFileSource(file, bufferSizeInKb);
  }

  public Name createIdentifier(String identifier) {
    return new MemName(identifier);
  }


}
