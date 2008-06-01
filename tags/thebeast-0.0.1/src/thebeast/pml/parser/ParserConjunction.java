package thebeast.pml.parser;

import java.util.List;
import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class ParserConjunction extends ParserFormula {


  final ParserFormula lhs,rhs;


  public ParserConjunction(ParserFormula lhs, ParserFormula rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {    
    visitor.visitConjuction(this);
  }


  public String toString() {
    return lhs + " & "  + rhs;  
  }
}
