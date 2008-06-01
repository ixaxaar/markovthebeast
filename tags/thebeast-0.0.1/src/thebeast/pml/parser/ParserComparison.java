package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 17-Feb-2007 Time: 15:09:12
 */
public class ParserComparison extends ParserFormula{

  public final Type type;
  public final ParserTerm lhs, rhs;

  public enum Type {
    EQ,LT,GT,LEQ,GEQ,NEQ
  }

  public ParserComparison(Type type, ParserTerm lhs, ParserTerm rhs) {
    this.type = type;
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitComparison(this);
  }
}
