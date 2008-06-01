package thebeast.pml.parser;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 20:13:13
 */
public class ParserCreatePredicate extends ParserStatement {

  final String name;
  final List<String> types;
  final int seq;

  public ParserCreatePredicate(String name, List<String> types, int seq) {
    this.name = name;
    this.types = types;
    this.seq = seq;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitCreatePredicate(this);
  }


  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("predicate ").append(name).append(" : ");
    int index = 0;
    for (String type : types){
      if (index++ > 0) result.append(" x ");
      result.append(type);
    }
    return result.toString();
  }
}
