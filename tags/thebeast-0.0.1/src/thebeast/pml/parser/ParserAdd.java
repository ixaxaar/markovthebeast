package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserAdd extends ParserTerm {

  final ParserTerm lhs,rhs;


  public ParserAdd(ParserTerm lhs, ParserTerm rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitParserAdd(this);
  }


  public String toString() {
    return lhs + " + " + rhs;
  }
}
