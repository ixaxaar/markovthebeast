package thebeast.pml.parser;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class ParserAtom extends ParserFormula {

  final String predicate;
  final List<ParserTerm> args;

  public ParserAtom(String predicate, List<ParserTerm> args) {
    this.predicate = predicate;
    this.args = args;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitAtom(this);
  }


  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(predicate).append("(");
    int index = 0;
    for (ParserTerm term : args){
      if (index++ > 0) result.append(", ");
      result.append(term);
    }
    result.append(")");
    return result.toString();
  }
}
