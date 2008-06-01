package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserIntConstant extends ParserTerm {

  final int number;

  public ParserIntConstant(int number) {
    this.number = number;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitIntConstant(this);
  }


  public String toString() {
    return String.valueOf(number);
  }
}
