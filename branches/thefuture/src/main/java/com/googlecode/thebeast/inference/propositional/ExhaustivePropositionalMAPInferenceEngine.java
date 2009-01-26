package com.googlecode.thebeast.inference.propositional;

import com.googlecode.thebeast.pml.AtomFormula;
import com.googlecode.thebeast.pml.GroundAtomAssignment;
import com.googlecode.thebeast.pml.GroundAtomNode;
import com.googlecode.thebeast.pml.GroundFactorGraph;
import com.googlecode.thebeast.pml.PMLVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class ExhaustivePropositionalMAPInferenceEngine implements PropositionalMAPInferenceEngine {

    private GroundFactorGraph groundFactorGraph;
    private GroundAtomAssignment observed;
    private PMLVector weights;
    private int evaluations = 0;
    private static Logger logger = LoggerFactory.getLogger(ExhaustivePropositionalMAPInferenceEngine.class);

    public ExhaustivePropositionalMAPInferenceEngine(
        final GroundFactorGraph groundFactorGraph,
        final PMLVector weights) {
        setGroundFactorGraph(groundFactorGraph);
        setWeights(weights);
    }

    public ExhaustivePropositionalMAPInferenceEngine() {
    }

    public GroundFactorGraph getGroundFactorGraph() {
        return groundFactorGraph;
    }

    public PropositionalMAPResult infer() {
        //build list of atoms to change
        logger.info("Started Inference");
        ArrayList<AtomFormula> hiddenAtoms = new ArrayList<AtomFormula>();
        for (GroundAtomNode node : groundFactorGraph.getNodes())
            if (!observed.hasValue(node.getAtom()))
                hiddenAtoms.add(node.getAtom());

        //create result with observed assignments
        GroundAtomAssignment currentAssignment = new GroundAtomAssignment(observed);

        //create result to return
        GroundAtomAssignment currentResult = new GroundAtomAssignment(currentAssignment);

        //initialize all hidden atoms to 0.0
        currentAssignment.setValue(hiddenAtoms, false);

        //max score so far
        double maxScore = Double.MIN_VALUE;

        //initialize number of evaluations
        evaluations = 0;

        //iterate over all possible assignments of the hidden atoms
        int size = hiddenAtoms.size();
        for (int flip = 0; flip < Math.pow(2, size); ++flip) {
            //extract feature vector
            PMLVector features = groundFactorGraph.extractFeatureVector(currentAssignment);
            ++evaluations;
            double score = weights.dotProduct(features);
            if (score > maxScore) {
                maxScore = score;
                currentResult = new GroundAtomAssignment(currentAssignment);
            }
            //flip
            for (int nodeIndex = 0; nodeIndex < size; ++nodeIndex) {
                if (flip % Math.pow(2, nodeIndex) == 0) {
                    AtomFormula node = hiddenAtoms.get(nodeIndex);
                    currentAssignment.setValue(node, !currentAssignment.getValue(node));
                }
            }
        }
        logger.info("Completed Inference after " + evaluations + " evaluations");
        return new PropositionalMAPResult(currentResult);
    }

    public void setGroundFactorGraph(GroundFactorGraph groundFactorGraph) {
        this.groundFactorGraph = groundFactorGraph;
    }

    public PMLVector getWeights() {
        return weights;
    }

    public void setWeights(PMLVector weights) {
        this.weights = weights;
    }

    public void setObservation(GroundAtomAssignment observation) {
        this.observed = observation;
    }

    public int getEvaluationCount() {
        return evaluations;
    }
}
