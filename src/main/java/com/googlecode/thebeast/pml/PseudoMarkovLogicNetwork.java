package com.googlecode.thebeast.pml;

/**
 * A PseudoMarkovLogicNetwork is a set of PseudoMLClause objects. It defines a
 * mapping from possible worlds to feature vectors. Note that in order to map a
 * possible world to a score/probability we need {@link
 * com.googlecode.thebeast.pml.Weights}, they are not included in a
 * PseudoMarkovLogicNetwork.
 * <p> A PseudoMarkovLogicNetwork differs from a
 * Markov Logic Network a la Richardson and Domingos in the following ways:
 * <il>
 *  <li>The type of allowed formulae is restricted.
 *  <li>Weights are separated from the definition of a PseudoMarkovLogicNetwork.
 * </il>
 *
 * @author Sebastian Riedel
 */
public class PseudoMarkovLogicNetwork {
}
