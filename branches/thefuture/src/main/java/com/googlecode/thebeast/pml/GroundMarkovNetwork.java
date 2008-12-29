package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.*;
import com.googlecode.thebeast.world.*;

import java.util.*;

/**
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
      int index =
        ((IntegerConstant) outer.get(clause.getIndexVariable())).getValue();
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
      node = new GroundNode(predicate, args);
      nodes.add(node);
      tuple2node.put(args, node);
    }
    return node;
  }

  public Collection<GroundNode> getNodes(Predicate predicate) {
    return Collections.unmodifiableCollection(
      nodeMapping.get(predicate).values());
  }

  public List<GroundNode> getNodes(){
    return Collections.unmodifiableList(nodes);
  }
  

}
