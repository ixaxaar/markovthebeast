package com.googlecode.thebeast.inference.propositional;

import com.googlecode.thebeast.pml.Assignment;
import com.googlecode.thebeast.pml.GroundMarkovNetwork;
import com.googlecode.thebeast.pml.PMLVector;
import com.googlecode.thebeast.inference.propositional.PropositionalMAPResult;

/**
 * @author Sebastian Riedel
 */
public interface PropositionalMAPInferenceEngine {

    PropositionalMAPResult infer();
    void setGroundMarkovNetwork(GroundMarkovNetwork groundMarkovNetwork);
    void setWeights(PMLVector weights);
    void setObservation(Assignment observation);

}
