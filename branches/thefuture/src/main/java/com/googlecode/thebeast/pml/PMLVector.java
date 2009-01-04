package com.googlecode.thebeast.pml;

import gnu.trove.TIntDoubleHashMap;
import gnu.trove.TIntDoubleProcedure;

import java.util.LinkedHashMap;

/**
 * @author Sebastian Riedel
 */
public class PMLVector {

  private LinkedHashMap<PMLClause, TIntDoubleHashMap>
    clause2Weights = new LinkedHashMap<PMLClause, TIntDoubleHashMap>();

  public double getValue(PMLClause clause, int index){
    TIntDoubleHashMap weights = clause2Weights.get(clause);
    if (weights == null) return 0;
    return weights.get(index);
  }

  public void setValue(PMLClause clause, int index, double value) {
    TIntDoubleHashMap weights = clause2Weights.get(clause);
    if (weights == null){
      weights = new TIntDoubleHashMap();
      clause2Weights.put(clause,weights);
    }
    weights.put(index,value);
  }

  public void addValue(PMLClause clause, int index, double value) {
    setValue(clause, index, getValue(clause, index) + value);
  }

  public double dotProduct(final PMLVector vector) {
    double result = 0;
    for (final PMLClause clause : clause2Weights.keySet()){
      DotProductCalculator calculator = new DotProductCalculator(vector, clause);
      TIntDoubleHashMap weights = clause2Weights.get(clause);
      weights.forEachEntry(calculator);
      result += calculator.result;
    }
    return result;
  }


  private static class DotProductCalculator implements TIntDoubleProcedure {
    double result;
    private final PMLVector vector;
    private final PMLClause clause;

    public DotProductCalculator(PMLVector vector, PMLClause clause) {
      this.vector = vector;
      this.clause = clause;
      result = 0;
    }

    public boolean execute(int i, double v) {
      result += vector.getValue(clause, i) * v;
      return true;
    }
  }
}
