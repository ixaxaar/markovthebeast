package thebeast.pml.formula;

/**
 * @author Sebastian Riedel
 */
public class True extends Atom {

  public void acceptAtomVisitor(AtomVisitor visitor) {
    visitor.visitTrue(this);
  }
}
