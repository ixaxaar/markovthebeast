package thebeast.pml.term;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Jan-2007 Time: 19:57:03
 */
public class DontCare extends Term {

  public final static DontCare DONTCARE = new DontCare();

  private DontCare() {
    super(null);
  }

  public void acceptTermVisitor(TermVisitor visitor) {
    visitor.visitDontCare(this);
  }

  public boolean isNonPositive() {
    return false;
  }

  public boolean isNonNegative() {
    return false;
  }
}
