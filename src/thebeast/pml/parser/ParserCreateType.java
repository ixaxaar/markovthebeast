package thebeast.pml.parser;

import java.util.List;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 20:01:42
 */
public class ParserCreateType extends ParserStatement {

  final String name;

  final boolean unknowns;

  final List<ParserNamedConstant> values;
  final List<String> names;

  public ParserCreateType(String name, boolean unknowns, List<ParserNamedConstant> values) {
    this.name = name;
    this.unknowns = unknowns;
    this.values = values;
    this.names = new LinkedList<String>();
    for (ParserNamedConstant constant : values)
      this.names.add(constant.name);
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitCreateType(this);
  }


  public List<String> getNames() {
    return names;
  }

  public String toString() {
    return "type " + name + " :" + (unknowns ? " ..." : " ") + values.toString();
  }
}
