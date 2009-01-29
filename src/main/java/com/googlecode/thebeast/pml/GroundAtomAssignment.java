package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.*;
import gnu.trove.TObjectIntHashMap;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public final class GroundAtomAssignment {


    private final GroundFactorGraph groundFactorGraph;

    private final TObjectIntHashMap<GroundAtomNode> values;


    public GroundAtomAssignment(GroundAtomAssignment original) {
        this.groundFactorGraph = original.groundFactorGraph;
        this.values = original.values.clone();
    }

    public GroundAtomAssignment(GroundFactorGraph groundFactorGraph, World world) {
        this(groundFactorGraph);
        for (UserPredicate pred : world.getSignature().getUserPredicates()) {
            Relation relation = world.getRelation(pred);
            for (Tuple tuple : relation) {
                setValue(groundFactorGraph.getNode(new AtomFormula(pred, tuple)), true);
            }
            if (!relation.isOpen()) {
                for (GroundAtomNode node : groundFactorGraph.getNodesForPredicate(pred)) {
                    if (!hasValue(node))
                        setValue(node, false);
                }
            }
        }
    }

    public GroundAtomAssignment(GroundFactorGraph groundFactorGraph) {
        this.groundFactorGraph = groundFactorGraph;
        this.values = new TObjectIntHashMap<GroundAtomNode>();
        for (GroundAtomNode node : groundFactorGraph.getNodes()) {
            if (node.getPredicate().isStatic()) {
                setValue(node, ((StaticPredicate) node.getPredicate()).
                    evaluate(node.getArguments()));
            }
        }

    }

    public boolean getValue(GroundAtomNode atom) {
        return values.get(atom) == 1;
    }

    public boolean hasValue(GroundAtomNode atom) {
        return values.containsKey(atom);
    }

    public void setValue(GroundAtomNode node, boolean value) {
        if (node == null) throw new IllegalArgumentException("Node must not be null");
        values.put(node, value ? 1 : 0);
    }

    public void setValue(Collection<GroundAtomNode> atoms, boolean value) {
        for (GroundAtomNode atom : atoms)
            setValue(atom, value);
    }

    public World createWorld(World parent) {
        World result = parent.getSignature().createWorld(parent);
        for (UserPredicate pred : parent.getSignature().getUserPredicates()) {
            Relation oldRelation = parent.getRelation(pred);
            Relation newRelation = result.getRelation(pred);
            if (oldRelation.isOpen()) {
                for (GroundAtomNode node : groundFactorGraph.getNodesForPredicate(pred)) {
                    if (getValue(node))
                        newRelation.add(node.getArguments());
                }
            }
            newRelation.setOpen(false);
        }

        return result;
    }

    public boolean getValue(Predicate predicate, Object... args) {
        GroundAtomNode node = groundFactorGraph.getNode(new AtomFormula(predicate, new Tuple(predicate, args)));
        if (node == null) throw new IllegalArgumentException("The given atom has no corresponding node in the " +
            "factor graph");
        return getValue(node);
    }

    public boolean hasValue(Predicate predicate, Object... args) {
        GroundAtomNode node = groundFactorGraph.getNode(new AtomFormula(predicate, new Tuple(predicate, args)));
        if (node == null) throw new IllegalArgumentException("The given atom has no corresponding node in the " +
            "factor graph");
        return hasValue(node);
    }

    public void setValue(boolean value, UserPredicate predicate, Object... args) {
        GroundAtomNode node = groundFactorGraph.getNode(new AtomFormula(predicate, new Tuple(predicate, args)));
        if (node == null) throw new IllegalArgumentException("The given atom has no corresponding node in the " +
            "factor graph");
        setValue(node, value);
    }

    public boolean getValue(AtomFormula atomFormula) {
        GroundAtomNode node = groundFactorGraph.getNode(atomFormula);
        if (node == null) throw new IllegalArgumentException("The given atom has no corresponding node in the " +
            "factor graph");
        return getValue(node);
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("(");
        if (values.size() > 0) {
            result.append(values.keys()[0]).append("->").append(values.get((GroundAtomNode) values.keys()[0]));
            for (int i = 1; i < values.size(); ++i)
                result.append(",").append(values.keys()[i]).append("->").
                    append(values.get((GroundAtomNode) values.keys()[i]));
        }
        result.append(")");
        return result.toString();
    }
}
