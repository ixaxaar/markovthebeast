package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Evaluation;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class DefaultPrinter implements GroundAtomsPrinter {
  public void print(GroundAtoms atoms, PrintStream out) {
    out.println(atoms.toString());
  }

  public void printEval(Evaluation evaluation, PrintStream out) {
    out.println(evaluation);

  }
}
