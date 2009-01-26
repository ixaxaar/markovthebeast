package com.googlecode.thebeast.inference.propositional;

import com.googlecode.thebeast.pml.GroundAtomAssignment;
import com.googlecode.thebeast.pml.GroundFactorGraph;
import com.googlecode.thebeast.pml.PMLVector;

/**
 * @author Sebastian Riedel
 */
public interface PropositionalMAPInferenceEngine {

    PropositionalMAPResult infer();
    void setGroundFactorGraph(GroundFactorGraph groundFactorGraph);
    void setWeights(PMLVector weights);
    void setObservation(GroundAtomAssignment observation);

}
