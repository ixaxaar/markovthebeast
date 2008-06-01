package thebeast.pml.parser;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 20-Feb-2007 Time: 17:36:00
 */
public class ParserSet extends ParserStatement {

  public final ParserName propertyName;
  public final Object value;

  public ParserSet(ParserName propertyName, Object value) {
    this.propertyName = propertyName;
    this.value = value;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitSet(this);
  }

  public List<Object> valueAsList(){
    return (List<Object>) value;
  }

}
