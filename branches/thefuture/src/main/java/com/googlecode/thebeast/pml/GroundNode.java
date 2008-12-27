package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.Tuple;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class GroundNode {

  private UserPredicate predicate;
  private Tuple arguments;

  private ArrayList<GroundFactor> connectedFactors;

}
