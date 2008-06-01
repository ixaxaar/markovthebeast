package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public interface Extractor {
  Collection<Integer> getColumns();

  void beginLine(int lineNr);

  void endLine(GroundAtoms atoms);

  void endSentence(GroundAtoms atoms);

  void extract(int column, String value);

}
