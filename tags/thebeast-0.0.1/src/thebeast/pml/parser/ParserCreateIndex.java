package thebeast.pml.parser;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 20:13:13
 */
public class ParserCreateIndex extends ParserStatement {

  final String name;
  final List<Boolean> markers;

  public ParserCreateIndex(String name, List<Boolean> markers) {
    this.name = name;
    this.markers = markers;
  }


  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitCreateIndex(this);
  }
}
