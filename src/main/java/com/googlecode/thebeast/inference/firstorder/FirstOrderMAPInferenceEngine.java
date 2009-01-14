package com.googlecode.thebeast.inference.firstorder;

import com.googlecode.thebeast.pml.PMLVector;
import com.googlecode.thebeast.pml.PseudoMarkovLogicNetwork;
import com.googlecode.thebeast.world.World;

/**
 * A FirstOrderMAPInferenceEngine takes a (pseudo) Markov Logic Network (MLN), a weight vector and an observation and
 * infers the most probable world with respect to the given MLN, weights and observation.
 *
 * <p>The core difference between a FirstOrderMAPInferenceEngine and a {@link com.googlecode.thebeast.inference.propositional.PropositionalMAPInferenceEngine}
 * is the fact that the former operates based on a first order description of a Markov Network (the Markov Logic
 * Network) while the latter operates directly on a (ground) Markov Network.
 *
 * <p>Note that implementations should always perform all major computational tasks within the {@link #infer()} method.
 * For example, an implementation must not instantiate a ground network in {@link #setPseudoMarkovLogicNetwork(com.googlecode.thebeast.pml.PseudoMarkovLogicNetwork)};
 *
 * @author Sebastian Riedel
 */
public interface FirstOrderMAPInferenceEngine {

    /**
     * Sets the weights to be considered in the next call to {@link #infer()}.
     *
     * @param weights the weights for the next inference call.
     */
    void setWeights(PMLVector weights);

    /**
     * Sets the PseudoMarkovLogicNetwork to be considered in the next call to {@link #infer()}.
     *
     * @param pseudoMarkovLogicNetwork the Markov Logic Network for the next inference call.
     */
    void setPseudoMarkovLogicNetwork(PseudoMarkovLogicNetwork pseudoMarkovLogicNetwork);

    /**
     * Sets the observation to be considered in the next call to {@link #infer()}
     *
     * @param observation the observation for the next inference call.
     */
    void setObservation(World observation);

    /**
     * Finds (or tries to find) the most likely possible world and returns it.
     *
     * @return a FirstOrderMAPResult that contains the most possible world with respect to the given mln, weights and
     *         observation.
     */
    FirstOrderMAPResult infer();

}
