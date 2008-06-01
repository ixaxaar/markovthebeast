package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserImport extends ParserStatement {

  final String filename;

  public ParserImport(String filename) {
    this.filename = filename;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitImport(this);
  }


  public String toString() {
    return "include " + filename;
  }
}
