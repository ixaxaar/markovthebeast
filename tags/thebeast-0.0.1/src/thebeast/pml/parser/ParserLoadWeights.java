package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserLoadWeights extends ParserStatement {

  public final String file;


  public ParserLoadWeights(String file) {
    this.file = file;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitLoadWeights(this);
  }
}
