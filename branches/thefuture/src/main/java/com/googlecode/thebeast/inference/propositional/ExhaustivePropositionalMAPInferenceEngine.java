package com.googlecode.thebeast.inference.propositional;

import com.googlecode.thebeast.pml.Assignment;
import com.googlecode.thebeast.pml.GroundMarkovNetwork;
import com.googlecode.thebeast.pml.GroundNode;
import com.googlecode.thebeast.pml.PMLVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class ExhaustivePropositionalMAPInferenceEngine implements PropositionalMAPInferenceEngine {

    private GroundMarkovNetwork groundMarkovNetwork;
    private Assignment observed;
    private PMLVector weights;
    private int evaluations = 0;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExhaustivePropositionalMAPInferenceEngine(GroundMarkovNetwork groundMarkovNetwork,
                               PMLVector weights) {
        setGroundMarkovNetwork(groundMarkovNetwork);
        setWeights(weights);
    }

    public ExhaustivePropositionalMAPInferenceEngine() {
    }

    public GroundMarkovNetwork getGroundMarkovNetwork() {
        return groundMarkovNetwork;
    }

    public PropositionalMAPResult infer() {
        //build list of atoms to change
        logger.info("Started Inference");
        ArrayList<GroundNode> hiddenNodes = new ArrayList<GroundNode>();
        for (GroundNode node : groundMarkovNetwork.getNodes())
            if (!observed.hasValue(node))
                hiddenNodes.add(node);

        //create result with observed assignments
        Assignment currentAssignment = new Assignment(observed);

        //create result to return
        Assignment currentResult = new Assignment(currentAssignment);

        //initialize all hidden atoms to 0.0
        currentAssignment.setValue(hiddenNodes, 0.0);

        //max score so far
        double maxScore = Double.MIN_VALUE;

        //initialize number of evaluations
        evaluations = 0;

        //iterate over all possible assignments of the hidden atoms
        int size = hiddenNodes.size();
        for (int flip = 0; flip < Math.pow(2, size); ++flip) {
            //extract feature vector
            PMLVector features = groundMarkovNetwork.extractFeatureVector(currentAssignment);
            ++evaluations;
            double score = weights.dotProduct(features);
            if (score > maxScore) {
                maxScore = score;
                currentResult = new Assignment(currentAssignment);
            }
            //flip
            for (int nodeIndex = 0; nodeIndex < size; ++nodeIndex) {
                if (flip % Math.pow(2, nodeIndex) == 0) {
                    GroundNode node = hiddenNodes.get(nodeIndex);
                    currentAssignment.setValue(node,
                        1.0 - currentAssignment.getValue(node));
                }
            }
        }
        logger.info("Completed Inference after " + evaluations + " evaluations");        
        return new PropositionalMAPResult(currentResult);
    }

    public void setGroundMarkovNetwork(GroundMarkovNetwork groundMarkovNetwork) {
        this.groundMarkovNetwork = groundMarkovNetwork;
    }

    public PMLVector getWeights() {
        return weights;
    }

    public void setWeights(PMLVector weights) {
        this.weights = weights;
    }

    public void setObservation(Assignment observation) {
        this.observed = observation;
    }

    public int getEvaluationCount() {
        return evaluations;
    }
}
