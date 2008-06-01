package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserTimes extends ParserTerm {

  final ParserTerm lhs,rhs;


  public ParserTimes(ParserTerm lhs, ParserTerm rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitParserTimes(this);
  }


  public String toString() {
    return lhs + " * " + rhs;
  }
}
