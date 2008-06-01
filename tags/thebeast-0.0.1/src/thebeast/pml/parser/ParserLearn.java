package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 20-Feb-2007 Time: 16:50:10
 */
public class ParserLearn extends ParserStatement {

  public final int instances, epochs;

  public ParserLearn(int instances, int epochs) {
    this.instances = instances;
    this.epochs = epochs;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitLearn(this);
  }
}
