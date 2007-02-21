package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Feb-2007 Time: 19:19:10
 */
public class ParserPrint extends ParserStatement {

  public final String target;
  public final boolean gold, scores;

  public ParserPrint(String target, boolean gold, boolean scores) {
    this.target = target;
    this.gold = gold;
    this.scores = scores;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitPrint(this);
  }
}
