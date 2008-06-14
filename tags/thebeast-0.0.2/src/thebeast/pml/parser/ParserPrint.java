package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Feb-2007 Time: 19:19:10
 */
public class ParserPrint extends ParserStatement {

  public final ParserName name;

  public ParserPrint(ParserName name) {
    this.name = name;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitPrint(this);
  }
}
