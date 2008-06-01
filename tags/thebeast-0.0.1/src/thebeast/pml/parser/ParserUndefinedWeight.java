package thebeast.pml.parser;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class ParserUndefinedWeight extends ParserFormula {

  final ParserFunctionApplication functionApplication;

  public ParserUndefinedWeight(ParserFunctionApplication functionApplication) {
    this.functionApplication = functionApplication;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitUndefinedWeight(this);
  }


  public String toString() {
    return "undefined(" + functionApplication.toString() + ")";
  }
}
