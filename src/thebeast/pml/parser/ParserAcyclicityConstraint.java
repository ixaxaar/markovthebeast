package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 17-Feb-2007 Time: 15:41:56
 */
public class ParserAcyclicityConstraint extends ParserFormula{

  public final String predicate;


  public ParserAcyclicityConstraint(String predicate) {
    this.predicate = predicate;
  }

  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitAcyclicityConstraint(this);    
  }
}
