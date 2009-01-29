package com.googlecode.thebeast.pml;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sebastian Riedel
 */
public final class GroundFactorGraph {

    private ArrayList<GroundFormulaFactor> factors = new ArrayList<GroundFormulaFactor>();
    private final Map<AtomFormula, GroundAtomNode>
        nodeMapping = new LinkedHashMap<AtomFormula, GroundAtomNode>();
    private final Set<GroundAtomNode> nodes = new LinkedHashSet<GroundAtomNode>();
    private final SetMultimap<Predicate, GroundAtomNode>
        predicate2node = new LinkedHashMultimap<Predicate, GroundAtomNode>();


    public List<GroundFormulaFactor> ground(final PMLFormula formula,
                                            final Iterable<NestedSubstitution> substitutions) {
        List<GroundFormulaFactor> result = new ArrayList<GroundFormulaFactor>();
        for (NestedSubstitution substitution : substitutions) {
            Formula groundFormula = new Grounder().ground(formula.getFormula(), substitution);
            List<AtomFormula> atoms = new AtomCollector().collect(groundFormula);
            List<GroundAtomNode> factorNodes = new ArrayList<GroundAtomNode>();
            for (AtomFormula atom : atoms)
                factorNodes.add(getNodeCreateIfNecessary(atom));
            GroundFormulaFactor factor = new GroundFormulaFactor(formula, groundFormula, substitution, factorNodes);
            factors.add(factor);
            result.add(factor);
        }
        return result;
    }

    public List<GroundFormulaFactor> ground(final PMLFormula formula,
                                            final String ... substitutionsStrings){
        return ground(formula,
            NestedSubstitution.createNestedSubstitutions(formula.getSignature(),substitutionsStrings));
    }

    public GroundAtomNode getNode(AtomFormula atom) {
        return nodeMapping.get(atom);
    }

    public GroundAtomNode getNode(Predicate predicate, Tuple args){
        return getNode(new AtomFormula(predicate, args));
    }

    private GroundAtomNode getNodeCreateIfNecessary(AtomFormula atom) {
        GroundAtomNode node = nodeMapping.get(atom);
        if (node == null) {
            node = new GroundAtomNode(atom);
            nodeMapping.put(atom, node);
            nodes.add(node);
            predicate2node.put(atom.getPredicate(), node);

        }
        return node;
    }

    public Set<GroundAtomNode> getNodes(){
        return Collections.unmodifiableSet(nodes);
    }

    public Set<GroundAtomNode> getNodesForPredicate(Predicate predicate){
        return Collections.unmodifiableSet(predicate2node.get(predicate));
    }

    public PMLVector extractFeatureVector(GroundAtomAssignment currentAssignment) {
        Evaluator evaluator = new Evaluator();
        PMLVector result = new PMLVector();
        for (GroundFormulaFactor factor : factors){
            result.addValue(factor.getPmlFormula(), factor.getFeatureIndex(),
                factor.getScale() * evaluator.evaluate(factor.getGroundFormula(),currentAssignment));    
        }
        return result;  
    }
}
