package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class PrinterCorpus extends AbstractCollection<GroundAtoms> implements Corpus {


  private GroundAtomsPrinter printer;
  private PrintStream out;


  public PrinterCorpus(GroundAtomsPrinter printer, PrintStream out) {
    this.printer = printer;
    this.out = out;
  }

  public Iterator<GroundAtoms> iterator() {
    return null;
  }

  public int size() {
    return 0;
  }

  public Signature getSignature() {
    return null;
  }

  public int getUsedMemory() {
    return 0;
  }

  public void append(GroundAtoms atoms) {
    printer.print(atoms, out);
  }
}
