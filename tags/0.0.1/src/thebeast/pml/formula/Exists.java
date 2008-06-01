package thebeast.pml.formula;

import thebeast.pml.Quantification;
import thebeast.pml.term.IntConstant;

/**
 * @author Sebastian Riedel
 */
public class Exists extends Atom {

  private Quantification quantification;
  private BooleanFormula formula;
  private boolean useClosure = false;


  public Exists(Quantification quantification, BooleanFormula formula) {
    this.quantification = quantification;
    this.formula = formula;
  }


  public Quantification getQuantification() {
    return quantification;
  }

  public BooleanFormula getFormula() {
    return formula;
  }

  public CardinalityConstraint toGEQConstraint(){
    return new CardinalityConstraint(new IntConstant(1),quantification,formula, IntConstant.MAX_VALUE, useClosure);
  }

  public void acceptAtomVisitor(AtomVisitor visitor) {
    visitor.visitExists(this);
  }
}
