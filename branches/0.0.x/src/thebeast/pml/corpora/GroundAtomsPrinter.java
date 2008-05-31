package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Evaluation;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public interface GroundAtomsPrinter {

  public void print(GroundAtoms atoms, PrintStream out);

  public void printEval(Evaluation evaluation, PrintStream out);

}
