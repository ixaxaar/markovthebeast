package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Feb-2007 Time: 19:19:10
 */
public class ParserTest extends ParserStatement {


  public final String mode;
  public final String file;

  public ParserTest(String mode, String file) {
    this.mode = mode;
    this.file = file;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitTest(this);
  }
}
