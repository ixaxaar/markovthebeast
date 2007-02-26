package thebeast.nod;

import thebeast.nod.statement.StatementFactory;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.variable.VariableFactory;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.expression.ExpressionFactory;
import thebeast.nod.identifier.IdentifierFactory;
import thebeast.nod.util.ExpressionBuilder;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface NoDServer {

  TypeFactory typeFactory();

  /**
   * A statement factory can be used to create reusable statements to be interpreted by the interpreter.
   *
   * @return the singleton statement factory of this server.
   */
  StatementFactory statementFactory();

  /**
   * The ExpressionFactory is an abstraction for creating expressions.
   *
   * @return the singleton expression factory for this server.
   */
  ExpressionFactory expressionFactory();

  /**
   * The ExpressionBuilder can be used to create expressions without
   * the need of many temporary expressions one would need if the
   * {@link thebeast.nod.NoDServer#expressionFactory()} was used.
   *
   * @return an ExpressionBuilder, will always be the same object.
   */
  ExpressionBuilder expressionBuilder();

  IdentifierFactory identifierFactory();

  /**
   * The interpreter is the main part of a No D server. It can be used
   * to execute statements such as creation of variables, assignment of values,
   * adding of indices etc.
   *
   * @return the singleton interpreter of this server.
   */
  Interpreter interpreter();

  Dump createDump(String filename, boolean createNew, int bufferSizeInKb);

}
