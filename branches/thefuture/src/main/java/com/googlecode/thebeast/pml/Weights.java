package com.googlecode.thebeast.pml;

import gnu.trove.TIntDoubleHashMap;

import java.util.LinkedHashMap;

/**
 * @author Sebastian Riedel
 */
public class Weights {

  private LinkedHashMap<PMLClause, TIntDoubleHashMap>
    clause2Weights = new LinkedHashMap<PMLClause, TIntDoubleHashMap>();

  public double getWeight(PMLClause clause, int index){
    TIntDoubleHashMap weights = clause2Weights.get(clause);
    if (weights == null) return 0;
    return weights.get(index);
  }

  public void setWeight(PMLClause clause, int index, double value) {
    TIntDoubleHashMap weights = clause2Weights.get(clause);
    if (weights == null){
      weights = new TIntDoubleHashMap();
      clause2Weights.put(clause,weights);
    }
    weights.put(index,value);

  }
}
