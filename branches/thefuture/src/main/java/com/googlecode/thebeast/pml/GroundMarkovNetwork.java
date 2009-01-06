package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.Substitution;
import com.googlecode.thebeast.query.Term;
import com.googlecode.thebeast.world.Constant;
import com.googlecode.thebeast.world.DoubleConstant;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A GroundMarkovNetwork is a Markov Network/factor graph in which factors
 * represent grounded versions of weighted first order formulae and nodes
 * represent ground atoms.
 *
 * @author Sebastian Riedel
 */
public class GroundMarkovNetwork {

  private final List<GroundFactor> factors = new ArrayList<GroundFactor>();
  private final List<GroundNode> nodes = new ArrayList<GroundNode>();

  private final Map<Predicate, Map<Tuple, GroundNode>>
    nodeMapping = new LinkedHashMap<Predicate, Map<Tuple, GroundNode>>();

  public List<GroundFactor> ground(
    final PMLClause clause,
    final Iterable<NestedSubstitution> substitutions) {

    ArrayList<GroundFactor> result = new ArrayList<GroundFactor>();
    for (NestedSubstitution substitution : substitutions) {
      Substitution outer = substitution.getOuterSubstitution();
      ArrayList<GroundNode> body = new ArrayList<GroundNode>();
      //get body nodes
      for (Atom atom : clause.getBody()) {
        ArrayList<Constant> constants =
          new ArrayList<Constant>(atom.getArguments().size());
        for (Term term : atom.getArguments()) {
          constants.add((Constant) outer.resolve(term));
        }
        body.add(getNode(atom.getPredicate(), new Tuple(constants)));
      }
      //get head atoms
      ArrayList<GroundNode> head = new ArrayList<GroundNode>();
      for (Substitution inner : substitution.getInnerSubstitutions()) {
        ArrayList<Constant> constants =
          new ArrayList<Constant>(clause.getHead().getArguments().size());
        for (Term term : clause.getHead().getArguments()) {
          constants.add((Constant) inner.resolveWithBackup(term, outer));
        }
        head.add(getNode(clause.getHead().getPredicate(), new Tuple(constants)));
      }
      FeatureIndex index =
        new FeatureIndex(outer.getSubset(clause.getIndexVariables()));
      double scale =
        ((DoubleConstant) outer.get(clause.getScaleVariable())).getValue();
      GroundFactor factor = new GroundFactor(body, head, clause, index, scale);
      factors.add(factor);
      result.add(factor);

    }
    return result;
  }

  public GroundNode getNode(Predicate predicate, Tuple args) {
    Map<Tuple, GroundNode> tuple2node = nodeMapping.get(predicate);
    if (tuple2node == null) {
      tuple2node = new LinkedHashMap<Tuple, GroundNode>();
      nodeMapping.put(predicate, tuple2node);
    }
    GroundNode node = tuple2node.get(args);
    if (node == null) {
      node = new GroundNode(predicate, args, nodes.size());
      nodes.add(node);
      tuple2node.put(args, node);
    }
    return node;
  }

  public Collection<GroundNode> getNodes(Predicate predicate) {
    return Collections.unmodifiableCollection(
      nodeMapping.get(predicate).values());
  }

  public List<GroundNode> getNodes() {
    return Collections.unmodifiableList(nodes);
  }

  public PMLVector extractFeatureVector(Assignment assignment) {
    //go over all factors and evaluate features.
    PMLVector result = new PMLVector();
    for (GroundFactor factor : factors) {
      result.addValue(factor.getClause(), factor.getIndex(),
        factor.evaluate(assignment) * factor.getScale());
    }
    return result;
  }


}