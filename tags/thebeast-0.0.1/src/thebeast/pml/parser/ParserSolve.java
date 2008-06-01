package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 19:58:56
 */
public class ParserSolve extends ParserStatement{

  public final int numIterations;

  public ParserSolve(int numIterations) {
    this.numIterations = numIterations;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitSolve(this);
  }
}
