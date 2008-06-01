package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserGenerateTypes extends ParserStatement {

  public final String file, generator;

  public ParserGenerateTypes(String generator, String file) {
    this.generator = generator;
    this.file = file;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitGenerateTypes(this);
  }
}
