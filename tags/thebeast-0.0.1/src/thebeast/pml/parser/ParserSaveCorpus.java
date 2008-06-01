package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserSaveCorpus extends ParserStatement {

  public final String file, factory;
  public final int from, to;


  public ParserSaveCorpus(String factory, String file) {
    this.factory = factory;
    this.file = file;
    from = -1;
    to = -1;
  }

  public ParserSaveCorpus(String factory, String file, int from, int to) {
    this.factory = factory;
    this.file = file;
    this.from = from;
    this.to = to;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitSaveCorpus(this);
  }
}
