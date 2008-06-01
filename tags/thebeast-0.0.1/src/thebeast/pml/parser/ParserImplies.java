package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserImplies extends ParserFormula {

  final ParserFormula lhs, rhs;

  public ParserImplies(ParserFormula lhs, ParserFormula rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitImplies(this);
  }


  public String toString() {
    return lhs + " => " + rhs;
  }
}
