package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 19:58:56
 */
public abstract class ParserStatement {

  public abstract void acceptParserStatementVisitor(ParserStatementVisitor visitor);

}
