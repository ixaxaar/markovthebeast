package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserDoubleConstant extends ParserTerm {

  final double number;

  public ParserDoubleConstant(double number) {
    this.number = number;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitDoubleConstant(this);
  }

  public String toString() {
    return String.valueOf(number);
  }
}
