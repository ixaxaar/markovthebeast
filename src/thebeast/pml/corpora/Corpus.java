package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.util.Collection;
import java.util.ListIterator;

/**
 * @author Sebastian Riedel
 */
public interface Corpus extends Collection<GroundAtoms> {

  Signature getSignature();

  ListIterator<GroundAtoms> listIterator();

  int getUsedMemory();


}
