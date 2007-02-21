package thebeast.pml.term;

import thebeast.pml.Type;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class BinnedInt extends Term {

  private Term argument;
  private List<Integer> bins;


  public BinnedInt(List<Integer> bins, Term argument) {
    super(Type.INT);
    this.bins = new ArrayList<Integer>(bins);
    this.argument = argument;
  }

  protected BinnedInt() {
    super(Type.INT);
  }

  public void acceptTermVisitor(TermVisitor visitor) {
    visitor.visitBinnedInt(this);
  }

  public boolean isNonPositive() {
    return false;
  }

  public boolean isNonNegative() {
    return false;
  }


  public Term getArgument() {
    return argument;
  }

  public List<Integer> getBins() {
    return bins;
  }
}
