package thebeast.pml.parser;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 20:17:18
 */
public class ParserCreateWeightFunction extends ParserStatement {

  final String name;
  final String returnType;
  final List<String> argTypes;

  public ParserCreateWeightFunction(String name, List<String> argTypes, String returnType) {
    this.name = name;
    this.argTypes = argTypes;
    this.returnType = returnType;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitCreateWeightFunction(this);
  }
   public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("weight ").append(name).append(" : ");
    int index = 0;
    for (String type : argTypes){
      if (index++ > 0) result.append(" x ");
      result.append(type);
    }
    if (argTypes.size() > 0) result.append(" -> ").append(returnType);
    return result.toString();
  }

  
}
