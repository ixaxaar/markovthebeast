package thebeast.pml.parser;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 17-Feb-2007 Time: 14:48:12
 */
public class ParserCardinalityConstraint extends ParserFormula {

  public final ParserFormula formula;
  public final ParserTerm lowerBound, upperBound;
  public final List<ParserTyping> quantification;


  public ParserCardinalityConstraint(ParserTerm lowerBound, List<ParserTyping> quantification,
                                     ParserFormula formula, ParserTerm upperBound) {
    this.lowerBound = lowerBound;
    this.quantification = quantification;
    this.formula = formula;
    this.upperBound = upperBound;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitCardinalityConstraint(this);
  }
}
