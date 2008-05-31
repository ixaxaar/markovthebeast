package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserMinus extends ParserTerm {

  final ParserTerm lhs,rhs;


  public ParserMinus(ParserTerm lhs, ParserTerm rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitParserMinus(this);
  }


  public String toString() {
    return lhs + " - " + rhs;
  }
}
