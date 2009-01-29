package com.googlecode.thebeast.inference.firstorder;

import com.googlecode.thebeast.inference.propositional.PropositionalMAPInferenceEngine;
import com.googlecode.thebeast.inference.propositional.PropositionalMAPResult;
import com.googlecode.thebeast.pml.*;
import com.googlecode.thebeast.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NaiveFirstOrderMAPInferenceEngine fully grounds a given Markov Logic Network and applies a specified
 * PropositionalMAPInferenceEngine to this network.
 *
 * <p>Usually this engine can only work for relatively simple MLNs that do not use very large types.
 *
 * @author Sebastian Riedel
 */
public class NaiveFirstOrderMAPInferenceEngine implements FirstOrderMAPInferenceEngine {

    private PMLVector weights;
    private PseudoMarkovLogicNetwork mln;
    private PropositionalMAPInferenceEngine propositionalMAPInferenceEngine;
    private World observation;
    private static Logger logger = LoggerFactory.getLogger(NaiveFirstOrderMAPInferenceEngine.class);

    /**
     * Creates a new NaiveFirstOrderMAPInferenceEngine for the given inference task.
     *
     * @param mln     the PseudoMarkovLogicNetwork to use.
     * @param weights the weights to use.
     * @param propositionalMAPInferenceEngine
     *                the propositionalMAPInferenceEngine  to use.
     */
    public NaiveFirstOrderMAPInferenceEngine(PseudoMarkovLogicNetwork mln,
                                             PMLVector weights,
                                             PropositionalMAPInferenceEngine propositionalMAPInferenceEngine) {
        this.mln = mln;
        this.weights = weights;
        this.propositionalMAPInferenceEngine = propositionalMAPInferenceEngine;
    }

    /**
     * Creates a NaiveFirstOrderMAPInferenceEngine with the given propositional solver.
     *
     * @param propositionalMAPInferenceEngine
     *         the propositional solver to be used for inference in the full ground network.
     */
    public NaiveFirstOrderMAPInferenceEngine(PropositionalMAPInferenceEngine propositionalMAPInferenceEngine) {
        this.propositionalMAPInferenceEngine = propositionalMAPInferenceEngine;
    }

    /**
     * {@inheritDoc}
     */
    public void setWeights(PMLVector weights) {
        this.weights = weights;
    }

    /**
     * {@inheritDoc}
     */
    public void setPseudoMarkovLogicNetwork(PseudoMarkovLogicNetwork pseudoMarkovLogicNetwork) {
        this.mln = pseudoMarkovLogicNetwork;
    }

    /**
     * {@inheritDoc}
     */
    public void setObservation(World observation) {
        this.observation = observation;
    }

    /**
     * {@inheritDoc}
     */
    public FirstOrderMAPResult infer() {
        logger.info("Starting inference");

        logger.debug("Fully grounding factor graph");
        GroundFactorGraph gmn = new GroundFactorGraph();
        for (PMLFormula formula : mln.getFormulas()) {
            gmn.ground(formula, PMLUtils.getAllSubstitutions(formula));
        }

        logger.debug("Converting relational world to node assignment");
        GroundAtomAssignment  observedAssignment =
            new GroundAtomAssignment(gmn, observation);

        logger.debug("Starting Propositional inference");
        propositionalMAPInferenceEngine.setGroundFactorGraph(gmn);
        propositionalMAPInferenceEngine.setObservation(observedAssignment);
        propositionalMAPInferenceEngine.setWeights(weights);
        PropositionalMAPResult propositionalResult = propositionalMAPInferenceEngine.infer();

        logger.debug("Converting node assignment to relational world");
        World result = propositionalResult.getAssignment().createWorld(observation);

        logger.info("Inference completed");
        return new FirstOrderMAPResult(result);
    }
}
