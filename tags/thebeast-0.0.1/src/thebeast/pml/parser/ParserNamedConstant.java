package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserNamedConstant extends ParserTerm {

  final String name;

  public ParserNamedConstant(String name) {
    this.name = name;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitNamedConstant(this);
  }

  public String toString() {
    return name;
  }
}
