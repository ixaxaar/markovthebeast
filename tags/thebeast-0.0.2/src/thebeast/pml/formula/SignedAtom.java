package thebeast.pml.formula;

/**
 * @author Sebastian Riedel
 */
public class SignedAtom {
  private Atom atom;
  private boolean sign;

  public SignedAtom(boolean sign, Atom atom) {
    this.sign = sign;
    this.atom = atom;
  }


  public Atom getAtom() {
    return atom;
  }

  public boolean isTrue() {
    return sign;
  }

  public String toString() {
    return (sign ? "" : "!") + atom;
  }
}
