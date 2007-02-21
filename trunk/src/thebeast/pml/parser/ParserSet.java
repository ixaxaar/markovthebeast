package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 20-Feb-2007 Time: 17:36:00
 */
public class ParserSet extends ParserStatement {

  public final String qualifier, property;
  public final int intValue;
  public final double doubleValue;
  public final String stringValue;


  public ParserSet(String qualifier, String property, int intValue) {
    this.qualifier = qualifier;
    this.property = property;
    this.intValue = intValue;
    this.doubleValue = -1;
    this.stringValue = null;
  }


  public ParserSet(String qualifier, String property, double doubleValue) {
    this.qualifier = qualifier;
    this.property = property;
    this.doubleValue = doubleValue;
    this.intValue = -1;
    this.stringValue = null;
  }


  public ParserSet(String qualifier, String property, String stringValue) {
    this.qualifier = qualifier;
    this.property = property;
    this.stringValue = stringValue;
    this.intValue = -1;
    this.doubleValue = -1;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitSet(this);
  }
}
