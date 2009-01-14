package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Relation;
import com.googlecode.thebeast.world.StaticPredicate;
import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.World;
import gnu.trove.TIntDoubleHashMap;
import gnu.trove.TIntHashSet;

import java.util.Collection;

/**
 * An Assignment assigns double values to ground nodes (atoms) of a ground Markov Network. By default each assignment
 * assigns 1.0 to all static predicate ground atoms.
 *
 * @author Sebastian Riedel
 */
public class Assignment {

    private TIntDoubleHashMap values = new TIntDoubleHashMap();
    private TIntHashSet keys = new TIntHashSet();
    private GroundMarkovNetwork groundMarkovNetwork;

    public Assignment(final Assignment original) {
        this.groundMarkovNetwork = original.groundMarkovNetwork;
        for (GroundNode node : groundMarkovNetwork.getNodes()) {
            if (original.hasValue(node))
                setValue(node, original.getValue(node));
        }
    }

    public Assignment(final GroundMarkovNetwork groundMarkovNetwork) {
        this.groundMarkovNetwork = groundMarkovNetwork;
        for (GroundNode node : groundMarkovNetwork.getNodes()) {
            if (node.getPredicate().isStatic()) {
                setValue(node, ((StaticPredicate) node.getPredicate()).
                    evaluate(node.getArguments()) ? 1.0 : 0.0);
            }
        }
    }

    public Assignment(final GroundMarkovNetwork groundMarkovNetwork,
                      final Collection<UserPredicate> alwaysTruePredicates) {
        this(groundMarkovNetwork);
        for (UserPredicate pred : alwaysTruePredicates) {
            for (GroundNode node : groundMarkovNetwork.getNodes(pred))
                setValue(node, 1.0);
        }
    }

    public Assignment(final GroundMarkovNetwork groundMarkovNetwork,
                      final World world) {
        this(groundMarkovNetwork);
        for (UserPredicate pred : world.getSignature().getUserPredicates()) {
            Relation relation = world.getRelation(pred);
            for (Tuple tuple : relation) {
                setValue(groundMarkovNetwork.getNode(pred, tuple), 1.0);
            }
            if (!relation.isOpen()) {
                for (GroundNode node : groundMarkovNetwork.getNodes(pred)) {
                    if (!hasValue(node))
                        setValue(node, 0.0);
                }
            }
        }
    }

    /**
     * Get the value for the given node.
     *
     * @param node the node to get the value for.
     * @return the value for the given node.
     */
    public double getValue(GroundNode node) {
        return values.get(node.getIndex());
    }

    public double getValue(Predicate predicate, Object... args) {
        return getValue(groundMarkovNetwork.getNode(predicate,
            new Tuple(predicate, args)));
    }

    public boolean hasValue(Predicate predicate, Object... args) {
        return hasValue(groundMarkovNetwork.getNode(predicate,
            new Tuple(predicate, args)));
    }

    public boolean hasValue(GroundNode node) {
        return keys.contains(node.getIndex());
    }

    public void setValue(GroundNode node, double value) {
        values.put(node.getIndex(), value);
        keys.add(node.getIndex());
    }

    public void setValue(Collection<GroundNode> nodes, double value) {
        for (GroundNode node : nodes)
            setValue(node, value);
    }

    public void setValue(double value, Predicate predicate, Object... args) {
        setValue(groundMarkovNetwork.getNode(predicate,
            new Tuple(predicate, args)), value);
    }

    public World createWorld(World parent) {
        World result = parent.getSignature().createWorld(parent);
        for (UserPredicate pred : parent.getSignature().getUserPredicates()) {
            Relation oldRelation = parent.getRelation(pred);
            Relation newRelation = result.getRelation(pred);
            if (oldRelation.isOpen()) {
                for (GroundNode node : groundMarkovNetwork.getNodes(pred)) {
                    if (getValue(node) == 1.0)
                        newRelation.add(node.getArguments());
                }
            }
            newRelation.setOpen(false);
        }

        return result;
    }
}
