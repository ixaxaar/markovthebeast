package thebeast.pml.formula;

import thebeast.pml.term.Term;
import thebeast.pml.term.IntConstant;
import thebeast.pml.term.FunctionApplication;
import thebeast.pml.Quantification;
import thebeast.pml.function.IntAdd;
import thebeast.pml.function.IntMinus;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 20:47:33
 */
public class CardinalityConstraint extends Atom {

  private Term lowerBound, upperBound;
  private Quantification quantification;
  private BooleanFormula formula;
  private boolean sign = true;
  private boolean useClosure = true;


  public CardinalityConstraint(Term lowerBound, Quantification quantification,
                               BooleanFormula formula, Term upperBound, boolean useClosure) {
    this.lowerBound = lowerBound;
    this.quantification = quantification;
    this.formula = formula;
    this.upperBound = upperBound;
    this.useClosure = useClosure;
  }


  public CardinalityConstraint(boolean sign, Term lowerBound, Quantification quantification,
                               BooleanFormula formula, Term upperBound, boolean useClosure) {
    this.sign = sign;
    this.lowerBound = lowerBound;
    this.quantification = quantification;
    this.formula = formula;
    this.upperBound = upperBound;
    this.useClosure = useClosure;
  }

  public CardinalityConstraint(boolean sign, CardinalityConstraint original) {
    this.sign = sign;
    this.lowerBound = original.getLowerBound();
    this.upperBound = original.getUpperBound();
    this.quantification = original.quantification;
    this.formula = original.formula;
  }

  public boolean getSign() {
    return sign;
  }

  public void acceptAtomVisitor(AtomVisitor visitor) {
    visitor.visitCardinalityConstraint(this);
  }

  public BooleanFormula getFormula() {
    return formula;
  }

  public Term getLowerBound() {
    return lowerBound;
  }

  public boolean useClosure() {
    return useClosure;
  }

  public Quantification getQuantification() {
    return quantification;
  }

  public Term getUpperBound() {
    return upperBound;
  }

  public boolean isLEQ() {
    return (lowerBound instanceof IntConstant && ((IntConstant) lowerBound).getInteger() == Integer.MIN_VALUE);
  }

  public boolean isGEQ() {
    return (upperBound instanceof IntConstant && ((IntConstant) upperBound).getInteger() == Integer.MAX_VALUE);
  }

  public CardinalityConstraint negate(){
    if (isLEQ()){
      return new CardinalityConstraint(new FunctionApplication(IntAdd.ADD, upperBound, new IntConstant(1)),
              quantification, formula, new IntConstant(Integer.MAX_VALUE), useClosure);
    } else if(isGEQ()){
      return new CardinalityConstraint(new IntConstant(Integer.MIN_VALUE),
              quantification, formula, new FunctionApplication(IntMinus.MINUS, lowerBound, new IntConstant(1)), useClosure);
    }
    throw new RuntimeException("Can't negate a cardinality constraint with upper and lower bound: " + this);
  }

}
