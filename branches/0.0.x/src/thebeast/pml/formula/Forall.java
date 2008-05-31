package thebeast.pml.formula;

import thebeast.pml.Quantification;
import thebeast.pml.term.IntConstant;

/**
 * @author Sebastian Riedel
 */
public class Forall extends Atom {

  private Quantification quantification;
  private BooleanFormula formula;
  private boolean useClosure = false;


  public Forall(Quantification quantification, BooleanFormula formula) {
    this.quantification = quantification;
    this.formula = formula;
  }


  public Quantification getQuantification() {
    return quantification;
  }

  public BooleanFormula getFormula() {
    return formula;
  }

  public CardinalityConstraint toLEQConstraint(){
    return new CardinalityConstraint(IntConstant.MIN_VALUE, quantification, new Not(formula), IntConstant.ZERO, useClosure);
  }

  public void acceptAtomVisitor(AtomVisitor visitor) {
    visitor.visitForall(this);
  }
}
