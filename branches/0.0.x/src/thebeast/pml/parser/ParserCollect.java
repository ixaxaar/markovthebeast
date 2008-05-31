package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 17-Feb-2007 Time: 19:03:41
 */
public class ParserCollect extends ParserStatement{
  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitCollect(this);
  }
}
