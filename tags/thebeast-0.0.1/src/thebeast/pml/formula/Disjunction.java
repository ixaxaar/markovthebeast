package thebeast.pml.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 25-Jan-2007 Time: 12:24:07
 */
public class Disjunction extends BooleanFormula {

  private ArrayList<BooleanFormula> arguments;

  public Disjunction(List<? extends BooleanFormula> arguments) {
    this.arguments = new ArrayList<BooleanFormula>(arguments);
  }

   public Disjunction(BooleanFormula ... arguments){
    this(Arrays.asList(arguments));
  }
  public void acceptBooleanFormulaVisitor(BooleanFormulaVisitor visitor) {
    visitor.visitDisjunction(this);
  }

  public List<BooleanFormula> getArguments() {
    return arguments;
  }
}
