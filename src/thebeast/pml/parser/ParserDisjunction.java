package thebeast.pml.parser;

import java.util.List;
import java.util.Arrays;

/**
 * @author Naoya Inoue
 */
public class ParserDisjunction extends ParserFormula {


  final ParserFormula lhs,rhs;


  public ParserDisjunction(ParserFormula lhs, ParserFormula rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {    
    visitor.visitDisjuction(this);
  }


  public String toString() {
    return lhs + " | "  + rhs;  
  }
}
