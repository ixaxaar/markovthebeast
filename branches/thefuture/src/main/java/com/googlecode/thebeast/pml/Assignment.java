package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.StaticPredicate;
import com.googlecode.thebeast.world.Tuple;
import gnu.trove.TIntDoubleHashMap;
import gnu.trove.TIntHashSet;

import java.util.Collection;

/**
 * An Assignment assigns double values to ground nodes (atoms). By default each
 * assignment assigns 1.0 to all static predicate ground atoms.
 *
 * @author Sebastian Riedel
 */
public class Assignment {

  private TIntDoubleHashMap values = new TIntDoubleHashMap();
  private TIntHashSet keys = new TIntHashSet();
  private GroundMarkovNetwork groundMarkovNetwork;


  public Assignment(Assignment original){
    this.groundMarkovNetwork = original.groundMarkovNetwork;
    for (GroundNode node : groundMarkovNetwork.getNodes()){
      if (original.hasValue(node))
        setValue(node,original.getValue(node));
    }
  }

  public Assignment(GroundMarkovNetwork groundMarkovNetwork) {
    this.groundMarkovNetwork = groundMarkovNetwork;
    for (GroundNode node : groundMarkovNetwork.getNodes()) {
      if (node.getPredicate().isStatic()) {
        setValue(node, ((StaticPredicate) node.getPredicate()).
          evaluate(node.getArguments()) ? 1.0 : 0.0);
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

  public double getValue(Predicate predicate, Object ... args){
    return getValue(groundMarkovNetwork.getNode(predicate,
      new Tuple(predicate,args)));
  }

  public boolean hasValue(GroundNode node) {
    return keys.contains(node.getIndex());
  }

  public void setValue(GroundNode node, double value) {
    values.put(node.getIndex(), value);
    keys.add(node.getIndex());
  }

  public void setValue(Collection<GroundNode> nodes, double value){
    for (GroundNode node : nodes)
      setValue(node, value);
  }

  public void setValue(double value, Predicate predicate, Object... args) {
    setValue(groundMarkovNetwork.getNode(predicate,
      new Tuple(predicate, args)), value);
  }

}
