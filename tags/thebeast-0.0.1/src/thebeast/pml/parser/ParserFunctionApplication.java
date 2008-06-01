package thebeast.pml.parser;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class ParserFunctionApplication extends ParserTerm {

  final String function;
  final List<ParserTerm> args;

  public ParserFunctionApplication(String function, List<ParserTerm> args) {
    this.function = function;
    this.args = args;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(function).append("(");
    int index = 0;
    for (ParserTerm term : args){
      if (index++ > 0) result.append(", ");
      result.append(term);
    }
    result.append(")");
    return result.toString();
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitFunctionApplication(this);
  }
}
