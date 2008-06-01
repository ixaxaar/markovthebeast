package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.util.Collection;

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
   * A rough estimate of how much memory the corpus uses.
   *
   * @return estimates memory usage in bytes.
   */
  int getUsedMemory();


  /**
   * Appends a ground atom collection to the end of this corpus.
   *
   * @param atoms the atoms to add.
   */
  void append(GroundAtoms atoms);


}
