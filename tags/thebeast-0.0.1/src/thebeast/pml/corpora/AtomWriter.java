package thebeast.pml.corpora;

import thebeast.pml.GroundAtom;

/**
 * @author Sebastian Riedel
 */
public interface AtomWriter {
   int getColumn();
   void write(GroundAtom atom, TabFormatCorpus.ColumnTable table);
 }
