package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.util.Collection;
import java.util.ListIterator;

/**
 * A Corpus is a collection of ground atoms (which a specific order).
 *
 * @author Sebastian Riedel
 */
public interface Corpus extends Collection<GroundAtoms> {

  /**
   * The signature atoms in this corpus belong to.
   *
   * @return the signature of this corpus.
   */
  Signature getSignature();

  /**
   * A list iterator over this corpus. Implementation is optional.
   *
   * @return a list iteration or null if implementation does not support it.
   */
  ListIterator<GroundAtoms> listIterator();

  /**
   * A rough estimate of how much memory the corpus uses.
   *
   * @return estimates memory usage in bytes.
   */
  int getUsedMemory();


}
