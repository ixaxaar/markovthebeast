package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserBoolConstant extends ParserTerm {

  final boolean value;


  public ParserBoolConstant(boolean value) {
    this.value = value;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitBoolConstant(this);
  }


  public String toString() {
    return String.valueOf(value);
  }
}
