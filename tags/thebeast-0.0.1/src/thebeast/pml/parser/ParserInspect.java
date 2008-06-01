package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserInspect extends ParserStatement {

  public final String target;
  public final boolean inspectType;


  public ParserInspect(String target, boolean inspectType) {
    this.target = target;
    this.inspectType = inspectType;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitInspect(this);
  }
}
