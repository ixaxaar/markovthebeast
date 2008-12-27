package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class GroundFactor {

  private List<GroundNode> condition;
  private List<GroundNode> domain;

  private PseudoMLClause clause;
  private NestedSubstitution substitution;
  
}
