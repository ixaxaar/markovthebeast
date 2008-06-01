package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserLoadScores extends ParserStatement {

  public final String file;


  public ParserLoadScores(String file) {
    this.file = file;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitLoadScores(this);
  }
}
