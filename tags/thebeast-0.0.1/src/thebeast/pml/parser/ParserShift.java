package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserShift extends ParserStatement {

  public final int delta;


  public ParserShift(int delta) {
    this.delta = delta;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitShift(this);
  }
}
