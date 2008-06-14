package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserSaveTypes extends ParserStatement {

  public final String file;


  public ParserSaveTypes(String file) {
    this.file = file;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitSaveTypes(this);
  }
}
