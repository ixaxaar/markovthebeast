package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitutionSet;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class PseudoGroundMarkovNetwork {

  private final List<GroundFactor> factors = new ArrayList<GroundFactor>();
  private final List<GroundNode> nodes = new ArrayList<GroundNode>();

  public List<GroundFactor> ground(final PseudoMLClause clause,
                                   final NestedSubstitutionSet substitutions) {
    
    return null;
  }

}
