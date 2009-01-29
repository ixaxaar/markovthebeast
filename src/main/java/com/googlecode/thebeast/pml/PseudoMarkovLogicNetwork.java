package com.googlecode.thebeast.pml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A PseudoMarkovLogicNetwork is a set of PMLFormula objects. It defines a mapping from possible worlds to feature
 * vectors. Note that in order to map a possible world to a score/probability we need {@link PMLVector}---weights are
 * not included in a PseudoMarkovLogicNetwork.
 *
 * <p> A PseudoMarkovLogicNetwork differs from a Markov Logic Network a la Richardson and Domingos in the following
 * ways:
 *
 * <il>
 *
 * <li>The type of allowed formulae is restricted.
 *
 * <li>Weights are separated from the definition of a PseudoMarkovLogicNetwork.
 *
 * </il>
 *
 * @author Sebastian Riedel
 */
public class PseudoMarkovLogicNetwork {

    private ArrayList<PMLFormula>
        formulas = new ArrayList<PMLFormula>();

    public void addFormula(PMLFormula clause) {
        formulas.add(clause);
    }

    public List<PMLFormula> getFormulas() {
        return Collections.unmodifiableList(formulas);
    }


}
