package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public interface GroundAtomsPrinter {

  public void print(GroundAtoms atoms, PrintStream out);
}
