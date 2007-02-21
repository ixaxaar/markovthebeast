package thebeast.pml.formula;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:27:09
 */
public abstract class Atom extends BooleanFormula {

  public void acceptBooleanFormulaVisitor(BooleanFormulaVisitor visitor) {
    visitor.visitAtom(this);
  }

  public abstract void acceptAtomVisitor(AtomVisitor visitor);

}
