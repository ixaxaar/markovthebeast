package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Feb-2007 Time: 19:19:10
 */
public class ParserLoad extends ParserStatement {

  public final ParserName target;
  public final String mode;
  public final String file;


  public ParserLoad(ParserName target, String mode, String file) {
    this.target = target;
    this.mode = mode;
    this.file = file;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitLoad(this);
  }
}
