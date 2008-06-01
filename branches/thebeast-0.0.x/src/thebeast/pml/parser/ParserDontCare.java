package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserDontCare extends ParserTerm {
  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitDontCare(this);
  }


  public String toString() {
    return "_";
  }
}
