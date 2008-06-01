package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Feb-2007 Time: 19:19:10
 */
public class ParserPrintWeights extends ParserStatement {

  public final String function;


  public ParserPrintWeights(String function) {
    this.function = function;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitPrintWeights(this);
  }
}
