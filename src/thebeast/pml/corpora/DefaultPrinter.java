package thebeast.pml.corpora;

import thebeast.pml.Evaluation;
import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;

import java.io.PrintStream;
import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public class DefaultPrinter implements GroundAtomsPrinter {


  private Collection<UserPredicate> predicates = null;


  public void print(GroundAtoms atoms, PrintStream out) {
    if (predicates == null)
      out.println(atoms.toString());
    else
      out.println(atoms.toString(predicates));
  }

  public void printEval(Evaluation evaluation, PrintStream out) {
    out.println(evaluation);

  }

  public Collection<UserPredicate> getPredicates() {
    return predicates;
  }

  public void setPredicates(Collection<UserPredicate> predicates) {
    this.predicates = predicates;
  }
}
