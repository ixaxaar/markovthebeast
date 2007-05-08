package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class DefaultPrinter implements GroundAtomsPrinter {
  public void print(GroundAtoms atoms, PrintStream out) {
    out.println(atoms.toString());
  }
}
