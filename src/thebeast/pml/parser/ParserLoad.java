package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 16-Feb-2007 Time: 19:19:10
 */
public class ParserLoad extends ParserStatement {

  public final String target;
  public final String file;
  public final boolean gold,corpus;

  public ParserLoad(String target, String file, boolean gold, boolean corpus) {
    this.target = target;
    this.file = file;
    this.gold = gold;
    this.corpus = corpus;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitLoad(this);
  }
}
