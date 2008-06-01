package thebeast.pml.parser;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 17-Feb-2007 Time: 15:41:56
 */
public class ParserAcyclicityConstraint extends ParserFormula{

  public final String predicate;
  public final List<Boolean> markers;


  public ParserAcyclicityConstraint(String predicate) {
    this.predicate = predicate;
    markers = new ArrayList<Boolean>();
    markers.add(true);
    markers.add(true);
    
  }

  public ParserAcyclicityConstraint(String predicate, List<Boolean> markers) {
    this.predicate = predicate;
    this.markers = new ArrayList<Boolean>(markers);    
  }


  public void acceptParserFormulaVisitor(ParserFormulaVisitor visitor) {
    visitor.visitAcyclicityConstraint(this);    
  }
}
