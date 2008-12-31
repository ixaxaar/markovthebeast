package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * @author Sebastian Riedel
 */
public final class GroundNode {

  private Predicate predicate;
  private Tuple arguments;
  private int index;

  private ArrayList<GroundFactor> connectedFactors;

  GroundNode(Predicate predicate, Tuple arguments, int index) {
    this.predicate = predicate;
    this.arguments = arguments;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  public Tuple getArguments() {
    return arguments;
  }

  public List<GroundFactor> getConnectedFactors(){
    return Collections.unmodifiableList(connectedFactors);
  }

  List<GroundFactor> getMutableConnectedFactors(){
    return connectedFactors;
  }

  
}
