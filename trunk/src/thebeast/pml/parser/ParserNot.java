package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserNot extends ParserFormula {


  final ParserFormula formula;


  public ParserNot(ParserFormula formula) {
    this.formula = formula;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitNot(this);
  }


  public String toString() {
    return "!" + formula;
  }
}
