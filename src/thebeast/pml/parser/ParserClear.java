package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 20-Feb-2007 Time: 18:22:43
 */
public class ParserClear extends ParserStatement {

  public final String what;


  public ParserClear(String what) {
    this.what = what;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitClear(this);
  }
}
