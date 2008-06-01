package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserVariable extends ParserTerm {

  final String name;


  public ParserVariable(String name) {
    this.name = name;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitVariable(this);
  }


  public String toString() {
    return name;
  }
}
