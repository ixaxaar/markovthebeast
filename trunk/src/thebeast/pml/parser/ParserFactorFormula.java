package thebeast.pml.parser;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class ParserFactorFormula extends ParserStatement {

  ParserTerm weight;
  ParserFormula condition;
  ParserFormula formula;
  List<ParserTyping> quantification;


  public ParserFactorFormula(List<ParserTyping> quantification, ParserFormula condition,
                             ParserFormula formula, ParserTerm weight) {
    this.quantification = quantification;
    this.condition = condition;
    this.formula = formula;
    this.weight = weight;
  }


  public String toString() {
    StringBuffer result = new StringBuffer("FOR ");
    int index = 0;
    for (ParserTyping typing : quantification){
      if (index ++ > 0) result.append(", ");
      result.append(typing);
    }
    if (condition != null) {
      result.append(" IF ");
      result.append(condition);
    }
    result.append(" ADD [");
    result.append(formula);
    result.append("] * ");
    result.append(weight);
    return result.toString();

  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitFactorFormula(this);
  }
}
